package net.inceptioncloud.dragonfly.overlay.hotaction

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.engine.internal.AssembledWidget
import net.inceptioncloud.dragonfly.engine.internal.Widget
import net.inceptioncloud.dragonfly.engine.widgets.assembled.RoundedRectangle
import org.apache.logging.log4j.LogManager

class HotActionWidget(
    val title: String = "Hot Action",
    val message: String = "This is a hot action",
    val actions: List<Action> = listOf()
) : AssembledWidget<HotActionWidget>() {

    val titleFontRenderer = Dragonfly.fontDesign.defaultFont.fontRendererAsync()

    init {
        if (actions.isEmpty()) {
            LogManager.getLogger().warn("No actions specified for hot action $title")
        }
    }

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "container" to RoundedRectangle()
    )

    override fun updateStructure() {
        updateWidget<RoundedRectangle>("container") {
            x = -5.0
            y = 10.0
            arc = 5.0
        }
    }

    override fun clone(): HotActionWidget {
        TODO("Not yet implemented")
    }

    override fun newInstance(): HotActionWidget = HotActionWidget()
}