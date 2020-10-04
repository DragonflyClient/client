package net.inceptioncloud.dragonfly.apps.modmanager

import net.inceptioncloud.dragonfly.mc
import net.inceptioncloud.dragonfly.mods.hotkeys.HotkeysMod
import net.inceptioncloud.dragonfly.mods.ege.EnhancedGameExperienceMod
import net.inceptioncloud.dragonfly.mods.nvidiahighlights.NvidiaHighlightsMod
import net.inceptioncloud.dragonfly.mods.keystrokes.KeystrokesMod
import net.inceptioncloud.dragonfly.mods.togglesneak.ToggleSneakMod
import net.inceptioncloud.dragonfly.ui.taskbar.TaskbarApp

object ModManagerApp : TaskbarApp("Mod Manager") {

    val availableMods = listOf(KeystrokesMod, HotkeysMod, NvidiaHighlightsMod, ToggleSneakMod, EnhancedGameExperienceMod)

    override fun open() = gui(ModManagerUI(mc.currentScreen))
}