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
    fun initializeTaskbar(gui: GuiScreen): Unit = with(gui) {
        val easing = EaseBack.withFactor(3.0).OUT
        val duration = 110

        val size = 55.0
        val space = 20.0
        val taskbarHeight = 80.0
        val exitOffset = 20.0

        +Rectangle {
            x = 0.0
            y = this@with.height.toDouble()
            width = this@with.width.toDouble()
            height = 0.0
            color = DragonflyPalette.accentNormal

            morph(
                duration, easing,
                Rectangle::height to taskbarHeight,
                Rectangle::y to this@with.height - taskbarHeight
            )?.start()
        } id "taskbar-background"

        val shadow = +Image {
            x = exitOffset + 3.0
            y = this@with.height.toDouble() + 3.0
            height = taskbarHeight - exitOffset * 2
            width = height
            resourceLocation = ResourceLocation("dragonflyres/icons/mainmenu/exit.png")
            color = WidgetColor(0, 0, 0, 50)

            morph(
                duration, easing,
                Image::y to this@with.height - taskbarHeight + exitOffset + 3.0
            )?.start()
        } id "taskbar-exit-game-shadow"

        +Image {
            x = shadow.x - 3.0
            y = shadow.y - 3.0
            height = shadow.height
            width = shadow.width
            resourceLocation = shadow.resourceLocation
            color = WidgetColor(255, 255, 255, 200)
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

            morph(
                duration, easing,
                Image::y to this@with.height - taskbarHeight + exitOffset
            )?.start()
        } id "taskbar-exit-game"

        var currentX = width / 2.0 - (taskbarApps.size * size + (taskbarApps.size - 1) * space) / 2.0

        for ((index, app) in taskbarApps.withIndex()) {
            val pos = currentX
            val appWidget = +TaskbarAppWidget(app) {
                x = currentX
                y = this@with.height - taskbarHeight + (taskbarHeight - size) / 2.0
                width = size
                height = size
            } id "app-${app.name.toLowerCase().replace(" ", "-")}"

            appWidget.apply {
                x += size / 2
                y += size / 2
                width = 0.0
                height = 0.0
                isVisible = false

                GraphicsEngine.runAfter(index * 50L + 300L) {
                    isVisible = true
                    morph(
                        80, easing,
                        TaskbarAppWidget::x to pos,
                        TaskbarAppWidget::y to this@with.height - taskbarHeight + (taskbarHeight - size) / 2.0,
                        TaskbarAppWidget::width to size,
                        TaskbarAppWidget::height to size
                    )?.start()
                }
            }

            currentX += size + space
        }
    }
}