package net.inceptioncloud.dragonfly.overlay.hotaction

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.font.FontWeight
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.internal.annotations.Interpolate
import net.inceptioncloud.dragonfly.engine.internal.annotations.State
import net.inceptioncloud.dragonfly.engine.structure.*
import net.inceptioncloud.dragonfly.engine.widgets.assembled.RoundedRectangle
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle
import net.inceptioncloud.dragonfly.options.sections.OptionsSectionOverlay
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import kotlin.properties.Delegates

/**
 * ## Hot Action Assembled Widget
 *
 * This widget is used to represent an active hot action in the top left corner of the screen.
 * It displays the [title] and [message] above all available [actions] in a container while being
 * fully responsive and adapting to the length of the [title], [message] and the amount of [actions].
 *
 * @property title the title of the hot action
 * @property message an additional message that describes the reason for the hot action's appearance
 * @property duration how many ticks the hot action will remain on the screen
 * @property actions a list of actions that can be executed
 */
class HotActionWidget(
    val title: String = "Hot Action",
    val message: String = "This is a hot action",
    val duration: Int = 200,
    val actions: List<Action> = listOf(),
    val allowMultipleActions: Boolean = false,
    initializerBlock: (HotActionWidget.() -> Unit)? = null
) : AssembledWidget<HotActionWidget>(initializerBlock), IPosition, IDimension {

    @Interpolate override var x: Double by property(0.0)
    @Interpolate override var y: Double by property(15.0)
    @Interpolate override var width: Double by property(-1.0)
    @Interpolate override var height: Double by property(-1.0)

    /**
     * The function that is used to convert an [Action] to a representing string
     */
    val joinFunc: (action: Action) -> CharSequence = { action ->
        "§#EF852E${actions.indexOf(action).let {
            if (OptionsSectionOverlay.hotActionsTriggerMode.key.get() == 0) {
                "F${it + 7}"
            } else {
                "${it + 1}."
            }
        }} §r${action.name}"
    }

    /**
     * The time of initialization that is used for the timer (set when invoking [updateStructure])
     */
    var initialTime by Delegates.notNull<Long>()

    /**
     * Whether the timer of the widget has already expired.
     */
    var expired = false

    init {
        if (actions.isEmpty()) {
            LogManager.getLogger().warn("No actions specified for hot action $title")
        }
    }

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "container" to RoundedRectangle(),
        "title" to TextField(),
        "message" to TextField(),
        "horizontal-rule" to Rectangle(),
        "actions" to TextField(),
        "timer-background" to Rectangle(),
        "timer" to Rectangle()
    )

    override fun updateStructure() {
        initialTime = System.currentTimeMillis()

        val messageFR = Dragonfly.fontManager.defaultFont.fontRenderer(size = 16)
        val titleFR = Dragonfly.fontManager.defaultFont.fontRenderer(fontWeight = FontWeight.MEDIUM, size = 20)

        val messageWidth = messageFR.getStringWidth(message)
        val titleWidth = titleFR.getStringWidth(title)
        val actionWidth = messageFR.getStringWidth(actions.joinToString(" ", transform = joinFunc))
        val containerWidth = listOf(messageWidth, titleWidth, actionWidth)
            .max()!!.coerceAtMost(200) + PADDING * 2.0

        val titleWidget = updateWidget<TextField>("title") {
            x = this@HotActionWidget.x
            y = this@HotActionWidget.y
            width = containerWidth - ARC
            padding = PADDING
            fontRenderer = titleFR
            staticText = title
            color = DragonflyPalette.foreground
            adaptHeight = true
        }!!.also { it.adaptHeight() }

        val messageWidget = updateWidget<TextField>("message") {
            x = this@HotActionWidget.x
            y = titleWidget.end()
            width = containerWidth - ARC
            padding = PADDING
            fontRenderer = messageFR
            staticText = message
            color = DragonflyPalette.foreground.altered { alphaDouble = 0.8 }
            adaptHeight = true
        }!!.also { it.adaptHeight() }

        val horizontalRule = updateWidget<Rectangle>("horizontal-rule") {
            x = this@HotActionWidget.x
            y = messageWidget.end() + 2.0
            width = containerWidth - ARC
            height = 0.5
            color = DragonflyPalette.foreground.altered { alphaDouble = 0.5 }
        }!!

        val actionsWidget = updateWidget<TextField>("actions") {
            x = this@HotActionWidget.x - 1.0
            y = horizontalRule.end() + 2.0
            width = containerWidth - ARC + 1.0
            padding = PADDING
            fontRenderer = messageFR
            textAlignHorizontal = Alignment.CENTER
            color = DragonflyPalette.foreground
            adaptHeight = true

            var string = ""
            var amountOfSpaces = 1

            while (messageFR.getStringWidth(string) < width - (padding * 2) - 5.0) {
                string = actions.joinToString(StringUtils.repeat(" ", ++amountOfSpaces), transform = joinFunc)
            }

            staticText = actions.joinToString(StringUtils.repeat(" ", amountOfSpaces - 1), transform = joinFunc)
        }!!.also { it.adaptHeight() }

        val containerWidget = updateWidget<RoundedRectangle>("container") {
            x = this@HotActionWidget.x - ARC
            y = this@HotActionWidget.y
            width = containerWidth
            height = actionsWidget.end() - this@HotActionWidget.y + 3.0
            arc = ARC
            color = DragonflyPalette.background.altered { alphaDouble = 0.8 }

            // assign the width and height to the global widget dimensions
            this@HotActionWidget.width = width
            this@HotActionWidget.height = height
        }!!

        val timerBackground = updateWidget<Rectangle>("timer-background") {
            width = containerWidth
            height = ARC
            x = this@HotActionWidget.x - ARC
            y = containerWidget.end() - height
            color = DragonflyPalette.background
        }!!

        updateWidget<Rectangle>("timer") {
            x = timerBackground.x
            y = timerBackground.y
            width = timerBackground.width
            height = timerBackground.height
            color = DragonflyPalette.accentNormal
        }!!.dynamic {
            val remaining = 1.0 - ((System.currentTimeMillis() - initialTime) / (duration * 5.0)).coerceIn(0.0, 1.0)
            width = timerBackground.width * remaining

            if (remaining == 0.0 && !expired) {
                HotAction.finish(this@HotActionWidget)
            }
        }
    }
}

/**
 * The amount of padding that the different text fields use
 */
const val PADDING = 1.5

/**
 * The corner-arc value of the hot action container
 */
const val ARC = 1.0

/**
 * Convenient function to get the vertical end of a widget using the position and the dimension
 * of the widget
 */
fun Widget<*>.end(): Double = if (this is IPosition && (this is IDimension || this is ISize)) {
    y + Defaults.getSizeOrDimension(this).second
} else error("You should not use this method on the widget $this")
