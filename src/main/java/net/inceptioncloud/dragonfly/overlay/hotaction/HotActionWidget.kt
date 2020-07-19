package net.inceptioncloud.dragonfly.overlay.hotaction

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.font.*
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.internal.annotations.State
import net.inceptioncloud.dragonfly.engine.widgets.assembled.RoundedRectangle
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.overlay.ScreenOverlay
import org.apache.logging.log4j.LogManager

class HotActionWidget(
    @property:State val title: String = "Hot Action",
    @property:State val message: String = "This is a hot action",
    @property:State val actions: List<Action> = listOf()
) : AssembledWidget<HotActionWidget>() {

    init {
        if (actions.isEmpty()) {
            LogManager.getLogger().warn("No actions specified for hot action $title")
        }
    }

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "container" to RoundedRectangle(),
        "title" to TextField(),
        "message" to TextField()
    )

    override fun updateStructure() {
        val messageFR: GlyphFontRenderer?
        var titleFR: GlyphFontRenderer? = null

        messageFR = Dragonfly.fontDesign.defaultFont.fontRendererAsync(size = 16) {
            println("Finished message font renderer!")
            if (titleFR != null) {
                continueUpdate(it, titleFR!!)
            }
        }
        titleFR = Dragonfly.fontDesign.defaultFont.fontRendererAsync(fontWeight = FontWeight.MEDIUM, size = 20) {
            println("Finished title font renderer!")
            if (messageFR != null) {
                continueUpdate(messageFR, it)
            }
        }
    }

    /**
     * Continues the process of [updateStructure] after the font renderer have been built
     * asynchronously.
     */
    private fun continueUpdate(messageFR: GlyphFontRenderer, titleFR: GlyphFontRenderer) {
        println("HELLO WORLD!")
        val messageWidth = messageFR.getStringWidth(message)
        val titleWidth = titleFR.getStringWidth(title)
        val actionWidth = actions.sumBy { messageFR.getStringWidth(it.name) } +
                (MIN_SPACE_BETWEEN_ACTIONS * actions.size - 1)

        val containerWidth = listOf(messageWidth, titleWidth, actionWidth)
            .max()!!.coerceAtMost((ScreenOverlay.dimensions.getWidth() / 5).toInt()) + PADDING * 2.0

        val titleWidget = updateWidget<TextField>("title") {
            x = 0.0
            y = 10.0
            width = containerWidth
            padding = PADDING
            fontRenderer = titleFR
            staticText = title
            color = DragonflyPalette.foreground
            adaptHeight = true
        }!!.also { it.adaptHeight() }

        val messageWidget = updateWidget<TextField>("message") {
            x = 0.0
            y = titleWidget.height.also { println("it = ${it}") }
            width = containerWidth
            padding = PADDING
            fontRenderer = messageFR
            staticText = message
            color = DragonflyPalette.foreground.apply { alphaDouble = 0.9 }
            adaptHeight = true
        }!!

        updateWidget<RoundedRectangle>("container") {
            x = -5.0
            y = 10.0
            width = containerWidth
            arc = 5.0
            color = DragonflyPalette.background.apply { alphaDouble = 0.8 }
        }
    }

    override fun clone(): HotActionWidget = HotActionWidget(
        title, message, actions
    )

    override fun newInstance(): HotActionWidget = HotActionWidget()
}

const val MIN_SPACE_BETWEEN_ACTIONS = 5

const val PADDING = 3.0