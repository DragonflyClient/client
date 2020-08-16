package net.inceptioncloud.dragonfly.ui.taskbar.apps

import net.inceptioncloud.dragonfly.ui.screens.AccountManagerUI
import net.inceptioncloud.dragonfly.ui.taskbar.TaskbarApp
import net.minecraft.client.Minecraft

object AccountManagerApp : TaskbarApp("Account Manager") {

    override fun open() = gui(AccountManagerUI(Minecraft.getMinecraft().currentScreen))
}