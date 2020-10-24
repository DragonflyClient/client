package net.inceptioncloud.dragonfly.engine.tooltip

import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.font.Typography
import net.inceptioncloud.dragonfly.engine.font.font
import net.inceptioncloud.dragonfly.engine.font.renderer.IFontRenderer
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.structure.IPosition
import net.inceptioncloud.dragonfly.engine.widgets.assembled.RoundedRectangle
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Polygon
import net.inceptioncloud.dragonfly.engine.widgets.primitive.TextRenderer
import net.inceptioncloud.dragonfly.mc

/**
 * ## TooltipWidget Assembled Widget
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
class TooltipWidget(
    initializerBlock: (TooltipWidget.() -> Unit)? = null
) : AssembledWidget<TooltipWidget>(initializerBlock), IPosition {

    override var x: Float by property(0.0F)
    override var y: Float by property(0.0F)

    val containerWidth: Float get() = fontRenderer.getStringWidth(text) + 4 * padding
    val containerHeight: Float get() = fontRenderer.height + 2 * padding
    val containerX: Float get() = this@TooltipWidget.x - containerWidth / 2
    val containerY: Float get() = this@TooltipWidget.y + verticalOffset

    var opacity: Float by property(0.0F)
    var arrowSize: Float by property(6.0F)

    var verticalOffset: Float by property(0.0F)
    var padding: Float by property(3.0F)
    var arc: Float by property(7.0F)

    var text: String by property("TooltipWidget")
    var alignment: TooltipAlignment by property(TooltipAlignment.ABOVE)
    var fontRenderer: IFontRenderer = font(Typography.SMALL)

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "background" to RoundedRectangle(),
        "arrow" to Polygon(),
        "text" to TextRenderer()
    )

    override fun updateStructure() {
        val background = "background"<RoundedRectangle> {
            arc = this@TooltipWidget.arc
            width = containerWidth
            height = containerHeight
            x = containerX.coerceIn(15.0F, (mc.currentScreen?.width ?: 1920) - 15.0F - containerWidth)
            y = containerY
            color = DragonflyPalette.foreground.altered { alphaFloat = opacity }
        } ?: return

        "arrow"<Polygon> {
            smooth = true
            color = DragonflyPalette.foreground.altered { alphaFloat = opacity }

            with(alignment) {
                points.clear()
                points.add(arrowPoint1().point)
                points.add(arrowPoint2().point)
                points.add(arrowPoint3().point)
            }
        }

        "text"<TextRenderer> {
            text = this@TooltipWidget.text
            x = background.x + padding * 2
            y = background.y + padding
            color = DragonflyPalette.background.altered { alphaFloat = opacity }
            fontRenderer = this@TooltipWidget.fontRenderer
        }
    }
}

private val Pair<Float, Float>.point: Point
    get() = Point(first, second)