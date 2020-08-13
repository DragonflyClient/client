package net.inceptioncloud.dragonfly.ui.taskbar

import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle
import net.inceptioncloud.dragonfly.ui.taskbar.widget.TaskbarAppWidget
import net.minecraft.client.gui.GuiScreen
import tornadofx.*

object Taskbar {

    private val taskbarApps = mutableListOf(
        TaskbarApp("Account Manager", "account-manager"),
        TaskbarApp("Mod Options", "mod-options"),
        TaskbarApp("Color Scheme", "color-scheme")
    )

    fun initializeTaskbar(gui: GuiScreen): Unit = with(gui) {
        val size = 18.0 * 2
        val space = 4.0 * 2
        val taskbarHeight = 25.0 * 2

        +Rectangle {
            x = 0.0
            y = this@with.height - taskbarHeight
            width = this@with.width.toDouble()
            height = taskbarHeight
            color = DragonflyPalette.accentNormal
        } id "taskbar-background"

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