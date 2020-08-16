package net.inceptioncloud.dragonfly.ui.taskbar.apps

import net.inceptioncloud.dragonfly.ui.screens.AboutUI
import net.inceptioncloud.dragonfly.ui.taskbar.TaskbarApp
import net.minecraft.client.Minecraft

object AboutDragonflyApp : TaskbarApp("About Dragonfly") {

    override fun open() = gui(AboutUI(Minecraft.getMinecraft().currentScreen))
}