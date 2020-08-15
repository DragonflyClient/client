package net.inceptioncloud.dragonfly.ui.taskbar.widget

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.GraphicsEngine
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.animation.post
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.internal.annotations.Interpolate
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseQuad
import net.inceptioncloud.dragonfly.engine.structure.IDimension
import net.inceptioncloud.dragonfly.engine.structure.IPosition
import net.inceptioncloud.dragonfly.engine.widgets.assembled.Tooltip
import net.inceptioncloud.dragonfly.engine.widgets.primitive.*
import net.inceptioncloud.dragonfly.overlay.ScreenOverlay
import net.inceptioncloud.dragonfly.ui.taskbar.TaskbarApp
import net.minecraft.client.Minecraft
import kotlin.math.*
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
 * @property isOpening whether the app is currently opening
 */
class TaskbarAppWidget(
    private val app: TaskbarApp,
    initializerBlock: (TaskbarAppWidget.() -> Unit)? = null
) : AssembledWidget<TaskbarAppWidget>(initializerBlock), IPosition, IDimension {

    @Interpolate override var x: Double by property(0.0)
    @Interpolate override var y: Double by property(0.0)
    @Interpolate override var width: Double by property(200.0)
    @Interpolate override var height: Double by property(20.0)

    var originX by Delegates.notNull<Double>()
    var originY by Delegates.notNull<Double>()
    var originWidth by Delegates.notNull<Double>()
    var originHeight by Delegates.notNull<Double>()

    var isHovered: Boolean = false
    var isOpening: Boolean = false

    val shadowOffset = 2.0
    val iconMargin = 6.0

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "shadow" to FilledCircle(),
        "background" to FilledCircle(),
        "icon" to Image(),
        "tooltip" to Tooltip()
    )

    override fun updateStructure() {
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
            x = this@TaskbarAppWidget.x + iconMargin
            y = this@TaskbarAppWidget.y + iconMargin
            width = this@TaskbarAppWidget.width - iconMargin * 2
            height = this@TaskbarAppWidget.height - iconMargin * 2
            resourceLocation = app.resourceLocation
        }

        "tooltip"<Tooltip> {
            text = app.name
            x = this@TaskbarAppWidget.originX + this@TaskbarAppWidget.originWidth / 2
            y = this@TaskbarAppWidget.originY - 50.0
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 40)
            padding = 3.0
            arc = 7.0
        }
    }

    override fun render() {
        super.render()

        if (isOpening || !isVisible)
            return

        val mouseX = GraphicsEngine.getMouseX()
        val mouseY = GraphicsEngine.getMouseY()
        val grow = 8.0

        if (mouseX in x..x + width && mouseY in y..y + height) {
            if (isHovered)
                return

            detachAnimation<MorphAnimation>()
            morph(
                30, EaseQuad.IN_OUT,
                TaskbarAppWidget::x to x - grow,
                TaskbarAppWidget::y to y - grow,
                TaskbarAppWidget::width to width + grow * 2,
                TaskbarAppWidget::height to height + grow * 2
            )?.post { _, _ -> morphTooltip(true) }?.start()
            isHovered = true
        } else {
            if (!isHovered)
                return

            detachAnimation<MorphAnimation>()
            morph(
                30, EaseQuad.IN_OUT,
                TaskbarAppWidget::x to originX,
                TaskbarAppWidget::y to originY,
                TaskbarAppWidget::width to originWidth,
                TaskbarAppWidget::height to originHeight
            )?.post { _, _ -> morphTooltip(false) }?.start()
            isHovered = false
        }
    }

    override fun handleStageAdd(stage: WidgetStage) {
        originX = x
        originY = y
        originWidth = width
        originHeight = height
    }

    override fun handleMousePress(data: MouseData) {
        if (isHovered && !isOpening && isVisible) {
            isOpening = true
            val screen = Minecraft.getMinecraft().currentScreen
            val centerX = x + width / 2
            val centerY = y + height / 2
            val distanceLeft = sqrt(centerX.pow(2) + centerY.pow(2))
            val distanceRight = sqrt((screen.width - centerX).pow(2) + centerY.pow(2))
            val targetSize = max(distanceLeft, distanceRight) * 2

            parentStage?.content
                ?.filterKeys { it.startsWith("app-") }
                ?.filterValues { it != this && it is TaskbarAppWidget }
                ?.forEach { (_, widget) -> widget.isVisible = false }

            morphTooltip(false)
            listOf("shadow", "background")
                .mapNotNull { getWidget<FilledCircle>(it) }
                .forEachIndexed { index, widget ->
                    widget.morph(
                        35, EaseQuad.IN_OUT,
                        FilledCircle::size to targetSize,
                        FilledCircle::x to widget.x - (targetSize / 2),
                        FilledCircle::y to widget.y - (targetSize / 2)
                    )?.post { _, _ ->
                        if (index != 0)
                            return@post

                        GraphicsEngine.runAfter(500) {
                            getWidget<Image>("icon")?.let { icon ->
                                icon.morph(
                                    50, EaseQuad.IN_OUT,
                                    Image::y to screen.height + 50.0
                                )?.start()
                            }?.post { _, _ ->
                                ScreenOverlay.setColorOverlay(DragonflyPalette.background)
                                Minecraft.getMinecraft().currentScreen.refresh()
                                app.open()
                                ScreenOverlay.getColorOverlay()?.morph(
                                    150, EaseQuad.IN_OUT,
                                    Rectangle::y to screen.height.toDouble(),
                                    Rectangle::height to 0.0
                                )?.post { _, _ ->
                                    ScreenOverlay.removeColorOverlay()
                                }?.start()
                            }?.start()
                        }
                    }?.start()
                }
            getWidget<Image>("icon")?.let {
                it.morph(
                    35, EaseQuad.IN_OUT,
                    Image::width to it.width * 1.5,
                    Image::height to it.height * 1.5,
                    Image::x to screen.width / 2.0 - it.width * 0.75,
                    Image::y to screen.height - it.height - 90.0
                )?.post { _, _ ->
                    getWidget<Tooltip>("tooltip")?.isVisible = false
                }?.start()
            }
        }
    }

    /**
     * Animates the tooltip of the app depending on whether it should be [shown][show].
     */
    private fun morphTooltip(show: Boolean) {
        val tooltip = getWidget<Tooltip>("tooltip") ?: return
        val offset = 10.0

        tooltip.detachAnimation<MorphAnimation>()
        tooltip.morph(
            40, EaseQuad.IN_OUT,
            tooltip::opacity to if (show) 1.0 else 0.0,
            tooltip::verticalOffset to if (show) -offset else 0.0
        )?.start()
    }
}