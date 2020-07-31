package net.inceptioncloud.dragonfly.overlay.hotaction

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.font.*
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.internal.annotations.Interpolate
import net.inceptioncloud.dragonfly.engine.internal.annotations.State
import net.inceptioncloud.dragonfly.engine.structure.*
import net.inceptioncloud.dragonfly.engine.widgets.assembled.RoundedRectangle
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle
import net.inceptioncloud.dragonfly.overlay.ScreenOverlay
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager

class HotActionWidget(
    @property:State val title: String = "Hot Action",
    @property:State val message: String = "This is a hot action",
    @property:State val duration: Int = 200,
    @property:State val actions: List<Action> = listOf(),

    @property:Interpolate val posX: Double = 0.0,
    @property:Interpolate val posY: Double = 10.0
) : AssembledWidget<HotActionWidget>() {

    val joinFunc: (action: Action) -> CharSequence = { "${actions.indexOf(it) + 1}. ${it.name}" }

    var initialTime = System.currentTimeMillis()

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
        "timer-container" to Rectangle(),
        "timer-background" to Rectangle(),
        "timer" to Rectangle()
    )

    override fun updateStructure() {
        initialTime = System.currentTimeMillis()

        val messageFR = Dragonfly.fontDesign.defaultFont.fontRenderer(size = 16)
        val titleFR = Dragonfly.fontDesign.defaultFont.fontRenderer(fontWeight = FontWeight.MEDIUM, size = 20)

        val messageWidth = messageFR.getStringWidth(message)
        val titleWidth = titleFR.getStringWidth(title)
        val actionWidth = messageFR.getStringWidth(actions.joinToString(" ", transform = joinFunc))
        val containerWidth = listOf(messageWidth, titleWidth, actionWidth)
            .max()!!.coerceAtMost(200) + PADDING * 2.0

        val titleWidget = updateWidget<TextField>("title") {
            x = posX
            y = posY
            width = containerWidth - ARC
            padding = PADDING
            fontRenderer = titleFR
            staticText = title
            color = DragonflyPalette.foreground
            adaptHeight = true
        }!!.also { it.adaptHeight() }

        val messageWidget = updateWidget<TextField>("message") {
            x = posX
            y = titleWidget.end()
            width = containerWidth - ARC
            padding = PADDING
            fontRenderer = messageFR
            staticText = message
            color = DragonflyPalette.foreground
            adaptHeight = true
        }!!.also { it.adaptHeight() }

        val horizontalRule = updateWidget<Rectangle>("horizontal-rule") {
            x = posX
            y = messageWidget.end() + 2.0
            width = containerWidth - ARC
            height = 0.5
            color = DragonflyPalette.foreground
        }!!

        val actionsWidget = updateWidget<TextField>("actions") {
            x = posX - 1.0
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
            x = posX - ARC
            y = posY
            width = containerWidth
            height = actionsWidget.end() - posY + 3.0
            arc = ARC
            color = DragonflyPalette.background
        }!!

        val timerContainer = updateWidget<Rectangle>("timer-container") {
            width = containerWidth
            height = ARC
            x = posX - ARC
            y = containerWidget.end() - height
            color = DragonflyPalette.background
        }!!

        updateWidget<Rectangle>("timer-background") {
            x = timerContainer.x
            y = timerContainer.y + 1.0
            width = timerContainer.width
            height = timerContainer.height - 1.0
            color = DragonflyPalette.accentDark
        }!!

        updateWidget<Rectangle>("timer") {
            x = timerContainer.x
            y = timerContainer.y + 1.0
            width = timerContainer.width
            height = timerContainer.height - 1.0
            color = DragonflyPalette.accentNormal
        }!!.dynamic {
            width = timerContainer.width * (1.0 - ((System.currentTimeMillis() - initialTime) / (duration * 5.0)).coerceIn(0.0, 1.0))
        }
    }

    override fun clone(): HotActionWidget = HotActionWidget(
        title, message, duration, actions
    )

    override fun newInstance(): HotActionWidget = HotActionWidget()
}

const val PADDING = 1.5

const val ARC = 2.0

fun Widget<*>.end(): Double = if (this is IPosition && (this is IDimension || this is ISize)) {
    this.y + Defaults.getSizeOrDimension(this).second
} else error("You should not use this method on the widget $this")