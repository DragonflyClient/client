package net.inceptioncloud.dragonfly.apps.accountmanager

import net.inceptioncloud.dragonfly.apps.accountmanager.AccountManagerUI
import net.inceptioncloud.dragonfly.ui.taskbar.TaskbarApp
import net.minecraft.client.Minecraft

object AccountManagerApp : TaskbarApp("Account Manager") {

    override fun open() = gui(
        AccountManagerUI(Minecraft.getMinecraft().currentScreen))
}