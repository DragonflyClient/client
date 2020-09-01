package net.inceptioncloud.dragonfly.apps.modmanager.controls.color

import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.structure.*
import net.inceptioncloud.dragonfly.engine.widgets.assembled.RoundedRectangle

class ColorPreview(
    initializerBlock: (ColorPreview.() -> Unit)? = null
) : AssembledWidget<ColorPreview>(initializerBlock), IPosition, IDimension, IColor {

    override var x: Double by property(0.0)
    override var y: Double by property(0.0)
    override var width: Double by property(0.0)
    override var height: Double by property(0.0)
    override var color: WidgetColor by property(DragonflyPalette.accentNormal)

    var containerColor: WidgetColor by property(WidgetColor(0xE5E5E5))
    var backgroundColor: WidgetColor by property(containerColor)
    var arc: Double by property(3.0)
    var borderSize: Double by property(4.0)

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "container" to RoundedRectangle(),
        "background" to RoundedRectangle(),
        "color" to RoundedRectangle()
    )

    override fun updateStructure() {
        "container"<RoundedRectangle> {
            x = this@ColorPreview.x
            y = this@ColorPreview.y
            width = this@ColorPreview.width
            height = this@ColorPreview.height
            arc = this@ColorPreview.arc
            color = containerColor
        }

        "background"<RoundedRectangle> {
            x = this@ColorPreview.x + borderSize
            y = this@ColorPreview.y + borderSize
            width = this@ColorPreview.width - 2 * borderSize
            height = this@ColorPreview.height - 2 * borderSize
            arc = this@ColorPreview.arc / 2.0
            color = backgroundColor
        }

        "color"<RoundedRectangle> {
            x = this@ColorPreview.x + borderSize
            y = this@ColorPreview.y + borderSize
            width = this@ColorPreview.width - 2 * borderSize
            height = this@ColorPreview.height - 2 * borderSize
            arc = this@ColorPreview.arc / 2.0
            color = this@ColorPreview.color
        }
    }
}