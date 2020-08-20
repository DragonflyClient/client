package net.inceptioncloud.dragonfly.apps.mods

import net.inceptioncloud.dragonfly.mc
import net.inceptioncloud.dragonfly.ui.taskbar.TaskbarApp

object ModManagerApp : TaskbarApp("Mod Manager") {

    override fun open() = gui(ModManagerUI(mc.currentScreen))

}