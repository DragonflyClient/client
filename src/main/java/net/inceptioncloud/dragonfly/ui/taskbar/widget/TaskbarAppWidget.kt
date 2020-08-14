package net.inceptioncloud.dragonfly.ui.taskbar.widget

import kotlinx.coroutines.yield
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.GraphicsEngine
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.internal.annotations.Interpolate
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseQuad
import net.inceptioncloud.dragonfly.engine.structure.IDimension
import net.inceptioncloud.dragonfly.engine.structure.IPosition
import net.inceptioncloud.dragonfly.engine.widgets.primitive.*
import net.inceptioncloud.dragonfly.ui.taskbar.TaskbarApp

class TaskbarAppWidget(
    private val app: TaskbarApp,
    initializerBlock: (TaskbarAppWidget.() -> Unit)? = null
) : AssembledWidget<TaskbarAppWidget>(initializerBlock), IPosition, IDimension {

    @Interpolate override var x: Double by property(0.0)
    @Interpolate override var y: Double by property(0.0)
    @Interpolate override var width: Double by property(200.0)
    @Interpolate override var height: Double by property(20.0)

    val originX = x
    val originY = y
    val originWidth = width
    val originHeight = height

    var isHovered: Boolean = false

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "shadow" to FilledCircle(),
        "background" to FilledCircle(),
        "icon" to Image()
    )

    override fun updateStructure() {
        val shadowOffset = 2.0

        "shadow"<FilledCircle> {
            x = this@TaskbarAppWidget.x - shadowOffset
            y = this@TaskbarAppWidget.y - shadowOffset
            size = this@TaskbarAppWidget.width + shadowOffset * 2
            color = DragonflyPalette.background.altered { alphaDouble = 0.9 }
            smooth = true
        }

        "background"<FilledCircle> {
            x = this@TaskbarAppWidget.x
            y = this@TaskbarAppWidget.y
            size = this@TaskbarAppWidget.width
            color = DragonflyPalette.background
            smooth = true
        }

        "icon"<Image> {
            x = this@TaskbarAppWidget.x + 3.0
            y = this@TaskbarAppWidget.y + 3.0
            width = this@TaskbarAppWidget.width - 6.0
            height = this@TaskbarAppWidget.height - 6.0
            resourceLocation = app.resourceLocation
        }
    }

    override fun handleMouseMove(data: MouseData) {
        val mouseX = GraphicsEngine.getMouseX()
        val mouseY = GraphicsEngine.getMouseY()
        val grow = 3.0

        if (mouseX in x..x + width && mouseY in y..y + height) {
            if (isHovered)
                return

            detachAnimation<MorphAnimation>()
            morph(
                60, EaseQuad.IN_OUT,
                TaskbarAppWidget::x to x - grow,
                TaskbarAppWidget::y to y - grow,
                TaskbarAppWidget::width to width + grow * 2,
                TaskbarAppWidget::height to height + grow * 2
            )?.start()
            isHovered = true
        } else {
            if (!isHovered)
                return

            detachAnimation<MorphAnimation>()
            morph(
                60, EaseQuad.IN_OUT,
                TaskbarAppWidget::x to originX,
                TaskbarAppWidget::y to originY,
                TaskbarAppWidget::width to originWidth,
                TaskbarAppWidget::height to originHeight
            )?.start()
            isHovered = false
        }
    }
}