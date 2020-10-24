package net.inceptioncloud.dragonfly.ui.taskbar

import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle
import net.inceptioncloud.dragonfly.apps.*
import net.inceptioncloud.dragonfly.apps.about.AboutDragonflyApp
import net.inceptioncloud.dragonfly.apps.accountmanager.AccountManagerApp
import net.inceptioncloud.dragonfly.apps.cosmetics.CosmeticsApp
import net.inceptioncloud.dragonfly.apps.modmanager.ModManagerApp
import net.inceptioncloud.dragonfly.apps.settings.DragonflySettingsApp
import net.inceptioncloud.dragonfly.engine.GraphicsEngine
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.internal.WidgetColor
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseBack
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseQuad
import net.inceptioncloud.dragonfly.engine.tooltip.Tooltip
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.inceptioncloud.dragonfly.mc
import net.inceptioncloud.dragonfly.ui.taskbar.widget.TaskbarAppWidget
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ResourceLocation

object Taskbar {

    /**
     * All available taskbar applications.
     */
    private val taskbarApps = listOf(
        DragonflySettingsApp,
        ModManagerApp,
        CosmeticsApp,
        AccountManagerApp,
        IdeasPlatformApp,
        AboutDragonflyApp
    )

    /**
     * Adds the taskbar to the stage of the given [gui] screen using its default fade in animation.
     */
    @JvmOverloads
    fun initializeTaskbar(gui: GuiScreen, forceAnimate: Boolean = false): Unit = with(gui) {
        val animate = mc.previousScreen?.stage?.get("taskbar-background") == null || forceAnimate

        val easing = EaseBack.withFactor(3.0).OUT
        val duration = 110

        val size = 55.0f
        val space = 20.0f
        val taskbarHeight = 80.0f
        val exitOffset = 20.0f

        +Rectangle {
            x = 0.0f
            y = this@with.height.toFloat()
            width = this@with.width.toFloat()
            height = 0.0f
            color = DragonflyPalette.accentNormal

            if (animate) {
                morph(
                    duration, easing,
                    Rectangle::height to taskbarHeight,
                    Rectangle::y to this@with.height - taskbarHeight
                )?.start()
            } else {
                height = taskbarHeight
                y = this@with.height - taskbarHeight
            }
        } id "taskbar-background"

        val shadow = +Image {
            x = exitOffset + 3.0f
            y = this@with.height.toFloat() + 3.0f
            height = taskbarHeight - exitOffset * 2
            width = height
            resourceLocation = ResourceLocation("dragonflyres/icons/mainmenu/exit.png")
            color = WidgetColor(0, 0, 0, 50)

            if (animate) {
                morph(
                    duration, easing,
                    Image::y to this@with.height - taskbarHeight + exitOffset + 3.0f
                )?.start()
            } else {
                y = this@with.height - taskbarHeight + exitOffset + 3.0f
            }
        } id "taskbar-exit-game-shadow"

        +Image {
            x = shadow.x - 3.0f
            y = shadow.y - 3.0f
            height = shadow.height
            width = shadow.width
            resourceLocation = shadow.resourceLocation
            color = WidgetColor(255, 255, 255, 200)
            tooltip = Tooltip("Close Dragonfly")
            clickAction = { mc.shutdown() }
            hoverAction = {
                if (it) {
                    detachAnimation<MorphAnimation>()
                    morph(40, EaseQuad.IN_OUT, Image::color to WidgetColor(255, 255, 255, 255))?.start()
                } else {
                    detachAnimation<MorphAnimation>()
                    morph(40, EaseQuad.IN_OUT, Image::color to WidgetColor(255, 255, 255, 200))?.start()
                }
            }

            if (animate) {
                morph(
                    duration, easing,
                    Image::y to this@with.height - taskbarHeight + exitOffset
                )?.start()
            } else {
                y = this@with.height - taskbarHeight + exitOffset
            }
        } id "taskbar-exit-game"

        var currentX = width / 2 - (taskbarApps.size * size + (taskbarApps.size - 1) * space) / 2

        for ((index, app) in taskbarApps.withIndex()) {
            val pos = currentX
            val appWidget = +TaskbarAppWidget(app) {
                x = currentX
                y = this@with.height - taskbarHeight + (taskbarHeight - size) / 2
                width = size
                height = size
            } id "app-${app.name.toLowerCase().replace(" ", "-")}"

            appWidget.apply {
                x += size / 2
                y += size / 2
                width = 0.0f
                height = 0.0f
                isVisible = false

                if (animate) {
                    GraphicsEngine.runAfter(index * 50L + 300L) {
                        isVisible = true
                        morph(
                            80, easing,
                            TaskbarAppWidget::x to pos,
                            TaskbarAppWidget::y to this@with.height - taskbarHeight + (taskbarHeight - size) / 2,
                            TaskbarAppWidget::width to size,
                            TaskbarAppWidget::height to size
                        )?.start()
                    }
                } else {
                    x = pos
                    y = this@with.height - taskbarHeight + (taskbarHeight - size) / 2
                    width = size
                    height = size
                    isVisible = true
                }
            }

            currentX += size + space
        }
    }
}