package net.inceptioncloud.minecraftmod.event.gui

import net.minecraft.client.gui.GuiMainMenu
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.multiplayer.GuiConnecting

/**
 * Called when the first gui should be opened after the game was started. Usually, the [target] gui is
 * the [GuiMainMenu] or the [GuiConnecting] if a server was specified at launch. Through this event, it
 * can be changed.
 *
 * @param target the original target [GuiScreen]
 */
data class StartupGuiEvent(var target: GuiScreen)