package net.inceptioncloud.dragonfly.apps.settings

import net.inceptioncloud.dragonfly.ui.taskbar.TaskbarApp
import net.minecraft.client.Minecraft

object DragonflySettingsApp : TaskbarApp("Dragonfly Settings") {

    override fun open() = gui(DragonflySettingsUI(Minecraft.getMinecraft().currentScreen))
}