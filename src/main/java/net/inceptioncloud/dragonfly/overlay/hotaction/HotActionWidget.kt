package net.inceptioncloud.dragonfly.overlay.hotaction

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.engine.font.*
import net.inceptioncloud.dragonfly.engine.internal.AssembledWidget
import net.inceptioncloud.dragonfly.engine.internal.Widget
import net.inceptioncloud.dragonfly.engine.widgets.assembled.RoundedRectangle
import org.apache.logging.log4j.LogManager
import kotlin.math.max

class HotActionWidget(
    val title: String = "Hot Action",
    val message: String = "This is a hot action",
    val actions: List<Action> = listOf()
) : AssembledWidget<HotActionWidget>() {

    init {
        if (actions.isEmpty()) {
            LogManager.getLogger().warn("No actions specified for hot action $title")
        }
    }

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "container" to RoundedRectangle()
    )

    override fun updateStructure() {
        val messageFR = Dragonfly.fontDesign.defaultFont.fontRendererAsync()
        Dragonfly.fontDesign.defaultFont.fontRendererAsync(fontWeight = FontWeight.MEDIUM, size = 24) {
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
        val messageWidth = messageFR.getStringWidth(message)
        val titleWidth = titleFR.getStringWidth(title)
        val actionWidth = actions.sumBy { messageFR.getStringWidth(it.name) } +
                (MIN_SPACE_BETWEEN_ACTIONS * actions.size - 1)

        val containerWidth = max(max(messageWidth, titleWidth), actionWidth)

        updateWidget<RoundedRectangle>("container") {
            x = -5.0
            y = 10.0
            width = containerWidth + 5.0
            arc = 5.0
        }
    }

    override fun clone(): HotActionWidget {
        TODO("Not yet implemented")
    }

    override fun newInstance(): HotActionWidget = HotActionWidget()
}

const val MIN_SPACE_BETWEEN_ACTIONS = 5