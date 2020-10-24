package net.inceptioncloud.dragonfly.ui.taskbar.widget

import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.animation.post
import net.inceptioncloud.dragonfly.engine.font.Typography
import net.inceptioncloud.dragonfly.engine.font.font
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseQuad
import net.inceptioncloud.dragonfly.engine.structure.IDimension
import net.inceptioncloud.dragonfly.engine.structure.IPosition
import net.inceptioncloud.dragonfly.engine.tooltip.TooltipWidget
import net.inceptioncloud.dragonfly.engine.widgets.primitive.FilledCircle
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.inceptioncloud.dragonfly.ui.taskbar.TaskbarApp
import kotlin.properties.Delegates

/**
 * ## Taskbar App Widget
 *
 * A widget that is used to render a taskbar app in the taskbar.
 *
 * @param app the app that this widget represents
 * @property originX the original x-coordinate of the widget that is saved for animation purposes
 * @property originY see [originX]
 * @property originWidth see [originX]
 * @property originHeight see [originX]
 * @property isHovered whether the widget is currently hovered
 * @property isPressed whether the app is currently opening
 */
class TaskbarAppWidget(
    private val app: TaskbarApp,
    initializerBlock: (TaskbarAppWidget.() -> Unit)? = null
) : AssembledWidget<TaskbarAppWidget>(initializerBlock), IPosition, IDimension {

    override var x: Float by property(0.0F)
    override var y: Float by property(0.0F)
    override var width: Float by property(200.0F)
    override var height: Float by property(20.0F)

    var backgroundColor: WidgetColor by property(DragonflyPalette.background)

    var originX by Delegates.notNull<Float>()
    var originY by Delegates.notNull<Float>()
    var originWidth by Delegates.notNull<Float>()
    var originHeight by Delegates.notNull<Float>()

    var isPressed: Boolean = false
    val pressGrow = 8.0f
    val hoverGrow = 6.0f

    val shadowOffset = 2.0f
    val iconMargin = 6.0f

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "shadow" to FilledCircle(),
        "background" to FilledCircle(),
        "icon" to Image(),
        "tooltip" to TooltipWidget()
    )

    override fun updateStructure() {
        "shadow"<FilledCircle> {
            x = this@TaskbarAppWidget.x - shadowOffset
            y = this@TaskbarAppWidget.y - shadowOffset
            size = this@TaskbarAppWidget.width + shadowOffset * 2
            color = backgroundColor.altered { alphaFloat = 0.9f }
            smooth = true
            isVisible = width > 0.0
        }

        "background"<FilledCircle> {
            x = this@TaskbarAppWidget.x
            y = this@TaskbarAppWidget.y
            size = this@TaskbarAppWidget.width
            color = backgroundColor
            smooth = true
        }

        "icon"<Image> {
            x = this@TaskbarAppWidget.x + iconMargin
            y = this@TaskbarAppWidget.y + iconMargin
            width = (this@TaskbarAppWidget.width - iconMargin * 2).coerceAtLeast(0.0f)
            height = (this@TaskbarAppWidget.height - iconMargin * 2).coerceAtLeast(0.0f)
            resourceLocation = app.resourceLocation
        }

        "tooltip"<TooltipWidget> {
            text = app.name
            x = this@TaskbarAppWidget.originX + this@TaskbarAppWidget.originWidth / 2
            y = this@TaskbarAppWidget.originY - 50.0f
            fontRenderer = font(Typography.SMALL)
            padding = 3.0f
            arc = 7.0f
        }
    }

    override fun handleStageAdd(stage: WidgetStage) {
        originX = x
        originY = y
        originWidth = width
        originHeight = height
        super.handleStageAdd(stage)
    }

    override fun canUpdateHoverState(): Boolean = !isPressed && isVisible

    override fun handleHoverStateUpdate() {
        if (isHovered) {
            detachAnimation<MorphAnimation>()
            morph(
                30, EaseQuad.IN_OUT,
                TaskbarAppWidget::x to x - hoverGrow,
                TaskbarAppWidget::y to y - hoverGrow,
                TaskbarAppWidget::width to width + hoverGrow * 2,
                TaskbarAppWidget::height to height + hoverGrow * 2
            )?.post { _, _ -> morphTooltip(true) }?.start()
        } else {
            detachAnimation<MorphAnimation>()
            morph(
                30, EaseQuad.IN_OUT,
                TaskbarAppWidget::x to originX,
                TaskbarAppWidget::y to originY,
                TaskbarAppWidget::width to originWidth,
                TaskbarAppWidget::height to originHeight
            )?.post { _, _ -> morphTooltip(false) }?.start()
        }
    }

    override fun handleMousePress(data: MouseData) {
        if (isHovered && !isPressed && isVisible) {
            isPressed = true

            detachAnimation<MorphAnimation>()
            morph(
                35, EaseQuad.OUT,
                TaskbarAppWidget::x to x + pressGrow,
                TaskbarAppWidget::y to y + pressGrow,
                TaskbarAppWidget::width to width - pressGrow * 2,
                TaskbarAppWidget::height to height - pressGrow * 2,
                TaskbarAppWidget::backgroundColor to DragonflyPalette.foreground
            )?.post { _, _ ->
                detachAnimation<MorphAnimation>()
                morph(
                    35, EaseQuad.IN,
                    TaskbarAppWidget::x to originX - hoverGrow,
                    TaskbarAppWidget::y to originY - hoverGrow,
                    TaskbarAppWidget::width to originWidth + hoverGrow * 2,
                    TaskbarAppWidget::height to originHeight + hoverGrow * 2,
                    TaskbarAppWidget::backgroundColor to DragonflyPalette.background
                )?.post { _, _ ->
                    app.open()
                    isPressed = false
                }?.start()
            }?.start()
        }
    }

    /**
     * Animates the tooltip of the app depending on whether it should be [shown][show].
     */
    private fun morphTooltip(show: Boolean) {
        val tooltip = getWidget<TooltipWidget>("tooltip") ?: return
        val offset = 10.0

        tooltip.detachAnimation<MorphAnimation>()
        tooltip.morph(
            40, EaseQuad.IN_OUT,
            TooltipWidget::opacity to if (show) 1.0 else 0.0,
            TooltipWidget::verticalOffset to if (show) -offset else 0.0
        )?.start()
    }
}