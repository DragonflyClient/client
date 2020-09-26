package net.inceptioncloud.dragonfly.apps.cosmetics

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.account.AuthenticationBridge
import net.inceptioncloud.dragonfly.cosmetics.logic.CosmeticsManager
import net.inceptioncloud.dragonfly.mc
import net.inceptioncloud.dragonfly.ui.taskbar.TaskbarApp
import net.minecraft.client.Minecraft

object CosmeticsApp : TaskbarApp("Cosmetics") {

    override fun open() {
        when {
            Dragonfly.account == null -> AuthenticationBridge.showLoginModal()
            CosmeticsManager.dragonflyAccountCosmetics?.isNotEmpty() == true -> gui(CosmeticsUI(mc.currentScreen))
            else -> gui(NoCosmeticsUI(mc.currentScreen))
        }
    }
}