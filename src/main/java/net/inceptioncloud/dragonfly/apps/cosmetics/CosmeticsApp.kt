package net.inceptioncloud.dragonfly.apps.cosmetics

import net.inceptioncloud.dragonfly.ui.taskbar.TaskbarApp
import net.minecraft.client.Minecraft

object CosmeticsApp : TaskbarApp("Cosmetics") {

    override fun open() = gui(CosmeticsUI(Minecraft.getMinecraft().currentScreen))
}