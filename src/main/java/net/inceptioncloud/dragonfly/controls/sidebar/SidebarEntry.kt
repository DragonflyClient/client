package net.inceptioncloud.dragonfly.controls.sidebar

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.structure.IColor
import net.inceptioncloud.dragonfly.engine.structure.IDimension
import net.inceptioncloud.dragonfly.engine.structure.IPosition
import net.inceptioncloud.dragonfly.engine.widgets.assembled.RoundRectangle
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image

class SidebarEntry(
    text: String,
    icon: ImageResource? = null,
    val metadata: Any? = null
) : AssembledWidget<SidebarEntry>(), IPosition, IDimension, IColor {

    override var x: Double by property(0.0)
    override var y: Double by property(0.0)
    override var width: Double by property(353.0)
    override var height: Double by property(53.0)

    override var color: WidgetColor by property(DragonflyPalette.background)

    var icon: ImageResource? by property(icon)
    var text: String by property(text)
    var isSelected = false

    var isSelectable: Boolean = true
    var iconMargin = 7.0

    /**
     * The sidebar manager that controls this entry.
     */
    lateinit var sidebarManager: SidebarManager

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "background" to RoundRectangle(),
        "icon" to Image(),
        "text" to TextField()
    )

    lateinit var textWidget: TextField
    lateinit var iconWidget: Image

    override fun updateStructure() {
        "background"<RoundRectangle> {
            x = this@SidebarEntry.x
            y = this@SidebarEntry.y
            width = this@SidebarEntry.width
            height = this@SidebarEntry.height
            color = this@SidebarEntry.color
            arc = 5.0
        }

        iconWidget = "icon"<Image> {
            if (icon == null) {
                x = this@SidebarEntry.x + 4.0
                width = 0.0
                isVisible = false
            } else {
                x = this@SidebarEntry.x + iconMargin
                y = this@SidebarEntry.y + iconMargin
                width = this@SidebarEntry.height - (iconMargin * 2)
                height = width
                resourceLocation = icon?.resourceLocation
                dynamicTexture = icon?.dynamicTexture
                isVisible = true
            }
        }!!

        textWidget = "text"<TextField> {
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
        }!!
    }

    override fun handleHoverStateUpdate() {
        tooltip?.animateTooltip(isHovered)
    }
}