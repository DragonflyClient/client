package net.inceptioncloud.dragonfly.ui.taskbar

import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle
import net.minecraft.client.gui.GuiScreen

object Taskbar {

    private val taskbarApps = mutableListOf(
        TaskbarApp("Account Manager", "account-manager"),
        TaskbarApp("Mod Options", "mod-options.png"),
        TaskbarApp("Color Scheme", "color-scheme")
    )

    fun initializeTaskbar(gui: GuiScreen): Unit = with(gui) {
        +Rectangle {
            x = 0.0
            y = this@with.height - 25.0
            width = this@with.width.toDouble()
            height = 25.0
            color = DragonflyPalette.accentNormal
        } id "taskbar-background"
    }
}