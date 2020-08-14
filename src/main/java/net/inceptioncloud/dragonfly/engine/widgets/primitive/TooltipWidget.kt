package net.inceptioncloud.dragonfly.engine.widgets.primitive

import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.font.renderer.IFontRenderer
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.internal.annotations.Interpolate
import net.inceptioncloud.dragonfly.engine.structure.IPosition
import net.inceptioncloud.dragonfly.engine.widgets.assembled.RoundedRectangle

/**
 * @property x the center-x coordinate of the tooltip
 */
class TooltipWidget(
    initializerBlock: (TooltipWidget.() -> Unit)? = null
) : AssembledWidget<TooltipWidget>(initializerBlock), IPosition {

    @Interpolate override var x: Double by property(0.0)
    @Interpolate override var y: Double by property(0.0)

    @Interpolate var opacity: Double by property(0.0)
    @Interpolate var arrowSize: Double by property(6.0)

    @Interpolate var verticalOffset: Double by property(0.0)
    @Interpolate var padding: Double by property(4.0)
    @Interpolate var arc: Double by property(10.0)

    var text: String by property("Tooltip")
    var fontRenderer: IFontRenderer? = null

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "background" to RoundedRectangle(),
        "arrow" to Polygon(),
        "text" to TextRenderer()
    )

    override fun updateStructure() {
        val textWidth = fontRenderer?.getStringWidth(text) ?: return

        val background = "background"<RoundedRectangle> {
            arc = this@TooltipWidget.arc
            width = textWidth + 2 * padding
            height = fontRenderer!!.height + 2 * padding
            x = this@TooltipWidget.x - width / 2
            y = this@TooltipWidget.y + verticalOffset
            color = DragonflyPalette.foreground.altered { alphaDouble = opacity }
        } ?: return

        "arrow"<Polygon> {
            smooth = true
            color = DragonflyPalette.foreground.altered { alphaDouble = opacity }

            val endY = background.y + background.height

            with(points) {
                clear()
                add(Point(x - arrowSize, endY))
                add(Point(x + arrowSize, endY))
                add(Point(x, endY + arrowSize))
            }
        }

        "text"<TextRenderer> {
            text = this@TooltipWidget.text
            x = background.x + padding
            y = background.y + padding
            color = DragonflyPalette.background.altered { alphaDouble = opacity }
            fontRenderer = this@TooltipWidget.fontRenderer!!
        }
    }
}