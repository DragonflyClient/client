package net.inceptioncloud.dragonfly.apps.modmanager.controls

import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.structure.*
import net.inceptioncloud.dragonfly.engine.widgets.assembled.RoundedRectangle

class ColorPreview(
    initializerBlock: (ColorPreview.() -> Unit)? = null
) : AssembledWidget<ColorPreview>(initializerBlock), IPosition, IDimension, IColor {

    override var x: Double by property(0.0)
    override var y: Double by property(0.0)
    override var width: Double by property(200.0)
    override var height: Double by property(20.0)
    override var color: WidgetColor by property(DragonflyPalette.accentNormal)

    var backgroundColor: WidgetColor by property(WidgetColor(0xE5E5E5))
    var arc: Double by property(3.0)

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "container" to RoundedRectangle(),
        "color" to RoundedRectangle()
    )

    override fun updateStructure() {
        val padding = 5.0

        "container"<RoundedRectangle> {
            x = this@ColorPreview.x
            y = this@ColorPreview.y
            width = this@ColorPreview.width
            height = this@ColorPreview.height
            arc = this@ColorPreview.arc
            color = backgroundColor
        }

        "color"<RoundedRectangle> {
            x = this@ColorPreview.x + padding
            y = this@ColorPreview.y + padding
            width = this@ColorPreview.width - 2 * padding
            height = this@ColorPreview.height - 2 * padding
            arc = this@ColorPreview.arc / 2.0
            color = this@ColorPreview.color
        }
    }
}