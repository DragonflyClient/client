package net.inceptioncloud.dragonfly.mods.hotkeys.types

import net.inceptioncloud.dragonfly.mods.hotkeys.Hotkey
import net.inceptioncloud.dragonfly.mods.hotkeys.types.config.ChatHotkeyConfig
import net.inceptioncloud.dragonfly.mods.hotkeys.types.data.HotkeyData
import net.minecraft.client.gui.GuiScreen.Companion.sendChatMessage

class ChatHotkey(
    data: HotkeyData,
    val config: ChatHotkeyConfig
) : Hotkey(data) {

    override fun actionPerformed() {
        sendChatMessage(config.message, false)
    }
}