package net.inceptioncloud.dragonfly.apps.modmanager

import net.inceptioncloud.dragonfly.mc
import net.inceptioncloud.dragonfly.mods.HotkeysMod
import net.inceptioncloud.dragonfly.mods.keystrokes.KeystrokesMod
import net.inceptioncloud.dragonfly.ui.taskbar.TaskbarApp

object ModManagerApp : TaskbarApp("Mod Manager") {

    val availableMods = listOf(KeystrokesMod, HotkeysMod)

    override fun open() = gui(ModManagerUI(mc.currentScreen))
}