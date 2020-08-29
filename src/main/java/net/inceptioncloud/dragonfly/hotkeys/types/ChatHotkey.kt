package net.inceptioncloud.dragonfly.hotkeys.types

import net.inceptioncloud.dragonfly.hotkeys.Hotkey
import net.inceptioncloud.dragonfly.hotkeys.HotkeyController
import net.inceptioncloud.dragonfly.hotkeys.types.config.ChatHotkeyConfig
import net.inceptioncloud.dragonfly.hotkeys.types.data.HotkeyData
import net.inceptioncloud.dragonfly.transition.number.SmoothDoubleTransition
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiScreen.Companion.sendChatMessage
import net.minecraft.client.gui.ScaledResolution
import java.awt.Color
import java.util.*

class ChatHotkey(
    data: HotkeyData,
    val config: ChatHotkeyConfig
) : Hotkey(data) {

    override fun actionPerformed() {
        sendChatMessage(config.message, false)
    }
}