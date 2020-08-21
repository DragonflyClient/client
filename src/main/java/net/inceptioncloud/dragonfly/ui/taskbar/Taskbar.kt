package net.inceptioncloud.dragonfly.ui.taskbar

import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle
import net.inceptioncloud.dragonfly.apps.*
import net.inceptioncloud.dragonfly.apps.about.AboutDragonflyApp
import net.inceptioncloud.dragonfly.apps.accountmanager.AccountManagerApp
import net.inceptioncloud.dragonfly.apps.settings.DragonflySettingsApp
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.internal.WidgetColor
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseQuad
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.inceptioncloud.dragonfly.mc
import net.inceptioncloud.dragonfly.ui.taskbar.widget.TaskbarAppWidget
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ResourceLocation

object Taskbar {

    private val taskbarApps = listOf(
        DragonflySettingsApp,
        AccountManagerApp,
        IdeasPlatformApp,
        AboutDragonflyApp
    )

    fun initializeTaskbar(gui: GuiScreen): Unit = with(gui) {
        val size = 55.0
        val space = 20.0
        val taskbarHeight = 80.0
        val exitOffset = 20.0

        +Rectangle {
            x = 0.0
            y = this@with.height - taskbarHeight
            width = this@with.width.toDouble()
            height = taskbarHeight
            color = DragonflyPalette.accentNormal
        } id "taskbar-background"

        val shadow = +Image {
            x = exitOffset + 3.0
            y = this@with.height - taskbarHeight + exitOffset + 3.0
            height = taskbarHeight - exitOffset * 2
            width = height
            resourceLocation = ResourceLocation("dragonflyres/icons/mainmenu/exit.png")
            color = WidgetColor(0, 0, 0, 50)
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
        } id "taskbar-exit-game"

        var currentX = width / 2.0 - (taskbarApps.size * size + (taskbarApps.size - 1) * space) / 2.0

        for (app in taskbarApps) {
            +TaskbarAppWidget(app) {
                x = currentX
                y = this@with.height - taskbarHeight + (taskbarHeight - size) / 2.0
                width = size
                height = size
            } id "app-${app.name.toLowerCase().replace(" ", "-")}"

            currentX += size + space
        }
    }
}