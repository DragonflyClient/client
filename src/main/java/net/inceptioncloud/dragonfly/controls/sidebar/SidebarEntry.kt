package net.inceptioncloud.dragonfly.controls.sidebar

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.internal.annotations.Interpolate
import net.inceptioncloud.dragonfly.engine.structure.IColor
import net.inceptioncloud.dragonfly.engine.structure.IDimension
import net.inceptioncloud.dragonfly.engine.structure.IPosition
import net.inceptioncloud.dragonfly.engine.widgets.assembled.RoundedRectangle
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.minecraft.util.ResourceLocation

class SidebarEntry(
    text: String,
    icon: ResourceLocation?,
    val metadata: Any? = null
) : AssembledWidget<SidebarEntry>(), IPosition, IDimension, IColor {

    override var x: Double by property(0.0)
    override var y: Double by property(0.0)
    override var width: Double by property(353.0)
    override var height: Double by property(53.0)

    @Interpolate
    override var color: WidgetColor by property(DragonflyPalette.background)

    var icon: ResourceLocation? by property(icon)
    var text: String by property(text)
    var selected = false

    /**
     * The sidebar manager that controls this entry.
     */
    lateinit var sidebarManager: SidebarManager

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "background" to RoundedRectangle(),
        "icon" to Image(),
        "text" to TextField()
    )

    override fun updateStructure() {
        val iconMargin = 7.0

        "background"<RoundedRectangle> {
            x = this@SidebarEntry.x
            y = this@SidebarEntry.y
            width = this@SidebarEntry.width
            height = this@SidebarEntry.height
            color = this@SidebarEntry.color
            arc = 5.0
        }

        val iconWidget = "icon"<Image> {
            x = this@SidebarEntry.x + iconMargin
            y = this@SidebarEntry.y + iconMargin
            width = this@SidebarEntry.height - (iconMargin * 2)
            height = width
            resourceLocation = icon
            isVisible = icon != null
        }!!

        "text"<TextField> {
            x = iconWidget.x + iconWidget.width + iconMargin
            y = this@SidebarEntry.y
            width = this@SidebarEntry.width - (x - this@SidebarEntry.x)
            height = this@SidebarEntry.height - 4
            staticText = text
            textAlignVertical = Alignment.CENTER
            textAlignHorizontal = Alignment.START
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 28 * 2, useScale = false)
            color = DragonflyPalette.foreground
            dropShadow = true
            shadowDistance = 2.0
            shadowColor = WidgetColor(0, 0, 0, 80)
        }
    }
}