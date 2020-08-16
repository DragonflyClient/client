package net.inceptioncloud.dragonfly.ui.taskbar.apps

import net.inceptioncloud.dragonfly.ui.screens.ModOptionsUI
import net.inceptioncloud.dragonfly.ui.taskbar.TaskbarApp
import net.minecraft.client.Minecraft

object DragonflySettingsApp : TaskbarApp("Dragonfly Settings") {

    override fun open() = gui(ModOptionsUI(Minecraft.getMinecraft().currentScreen))
}