package net.inceptioncloud.dragonfly.apps.cosmetics

import net.inceptioncloud.dragonfly.cosmetics.logic.CosmeticsManager
import net.inceptioncloud.dragonfly.ui.taskbar.TaskbarApp
import net.minecraft.client.Minecraft

object CosmeticsApp : TaskbarApp("Cosmetics") {

    override fun open() {
        val cosmetics = CosmeticsManager.dragonflyAccountCosmetics
        if (cosmetics?.isNotEmpty() == true) {
            gui(CosmeticsUI(Minecraft.getMinecraft().currentScreen))
        } else {
            gui(NoCosmeticsUI(Minecraft.getMinecraft().currentScreen))
        }
    }
}