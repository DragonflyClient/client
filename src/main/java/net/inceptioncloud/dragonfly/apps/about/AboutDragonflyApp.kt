package net.inceptioncloud.dragonfly.apps.about

import net.inceptioncloud.dragonfly.ui.taskbar.TaskbarApp
import net.minecraft.client.Minecraft

object AboutDragonflyApp : TaskbarApp("About Dragonfly") {

    override fun open() = gui(AboutDragonflyUI(Minecraft.getMinecraft().currentScreen))
}