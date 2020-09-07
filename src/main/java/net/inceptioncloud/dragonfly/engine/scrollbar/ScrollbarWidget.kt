package net.inceptioncloud.dragonfly.engine.scrollbar

import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.structure.*
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle

class ScrollbarWidget(
    val scrollbar: Scrollbar
) : AssembledWidget<ScrollbarWidget>(), IPosition, IDimension, IColor {

    override var x: Double by property(0.0)
    override var y: Double by property(0.0)
    override var width: Double by property(0.0)
    override var height: Double by property(0.0)
    override var color: WidgetColor by property(DragonflyPalette.accentNormal)

    var backgroundColor: WidgetColor by property(DragonflyPalette.background.altered { alphaDouble = 0.2 })

    var screenHeight: Double by property(0.0)
    var contentHeight: Double by property(0.0)
    var currentY: Double by property(0.0)

    var isEnabled by property(false)

    var visiblePart = 0.0
    var verticalPositionPart = 0.0
    var progress = 0.0

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "slider-background" to Rectangle(),
        "slider-foreground" to Rectangle()
    )

    override fun updateStructure() {
        if (!isEnabled) {
            "slider-background"<Rectangle> { isVisible = false }
            "slider-foreground"<Rectangle> { isVisible = false }
            return
        }

        visiblePart = screenHeight / contentHeight
        verticalPositionPart = currentY / contentHeight
        progress = currentY / (contentHeight - screenHeight)

        "slider-background"<Rectangle> {
            isVisible = true
            x = this@ScrollbarWidget.x
            y = this@ScrollbarWidget.y
            width = this@ScrollbarWidget.width
            height = this@ScrollbarWidget.height
            color = this@ScrollbarWidget.backgroundColor
        }

        "slider-foreground"<Rectangle> {
            isVisible = true
            x = this@ScrollbarWidget.x
            width = this@ScrollbarWidget.width
            y = this@ScrollbarWidget.y + this@ScrollbarWidget.height * verticalPositionPart
            height = this@ScrollbarWidget.height * visiblePart
            color = this@ScrollbarWidget.color
        }
    }

    override fun update() {
        scrollbar.update()
        super.update()
    }
}