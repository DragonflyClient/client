package net.inceptioncloud.dragonfly.engine.widgets.assembled

import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.font.renderer.IFontRenderer
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.structure.IPosition
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Polygon
import net.inceptioncloud.dragonfly.engine.widgets.primitive.TextRenderer
import net.inceptioncloud.dragonfly.utils.Keep

/**
 * ## Tooltip Assembled Widget
 *
 * A simple widget whose purpose is to act as a tooltip for other elements in the ui.
 * It has some useful properties that allow a rich set of animations to be applied to it.
 *
 * @property x the **center**-x coordinate of the tooltip
 * @property y the top-y coordinate of the tooltip
 * @property opacity the opacity value that is applied to all components of the widget
 * @property arrowSize sets the height of the arrow to the value and the width to double the value
 * @property verticalOffset a vertical offset that is added to the y-position of all components that
 * is intended so simplify animating the tooltip
 * @property padding the space between the background and the text (horizontal: multiplied by 2)
 * @property arc the arc-size of the background
 * @property text the text that the tooltip shows
 * @property fontRenderer the font renderer that is used to render the tooltip text
 */
class Tooltip(
    initializerBlock: (Tooltip.() -> Unit)? = null
) : AssembledWidget<Tooltip>(initializerBlock), IPosition {

    override var x: Double by property(0.0)
    override var y: Double by property(0.0)

    var opacity: Double by property(0.0)
    var arrowSize: Double by property(6.0)

    var verticalOffset: Double by property(0.0)
    var padding: Double by property(4.0)
    var arc: Double by property(10.0)

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
            arc = this@Tooltip.arc
            width = textWidth + 4 * padding
            height = fontRenderer!!.height + 2 * padding
            x = this@Tooltip.x - width / 2
            y = this@Tooltip.y + verticalOffset
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
            text = this@Tooltip.text
            x = background.x + padding * 2
            y = background.y + padding
            color = DragonflyPalette.background.altered { alphaDouble = opacity }
            fontRenderer = this@Tooltip.fontRenderer!!
        }
    }
}