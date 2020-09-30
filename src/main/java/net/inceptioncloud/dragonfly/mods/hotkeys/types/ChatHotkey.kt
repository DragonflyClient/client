package net.inceptioncloud.dragonfly.mods.hotkeys.types

import net.inceptioncloud.dragonfly.mods.hotkeys.Hotkey
import net.inceptioncloud.dragonfly.mods.hotkeys.types.config.ChatHotkeyConfig
import net.inceptioncloud.dragonfly.mods.hotkeys.types.data.HotkeyData
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.gui.GuiScreen.Companion.sendChatMessage
import java.util.*

class ChatHotkey(
    data: HotkeyData,
    val config: ChatHotkeyConfig
) : Hotkey(data) {

    override fun actionPerformed() {
        if (config.sendInstant) {
            sendChatMessage(config.message, false)
        } else {
            Minecraft.getMinecraft().displayGuiScreen(GuiChat())
            if (Minecraft.getMinecraft().currentScreen is GuiChat) {
                (Minecraft.getMinecraft().currentScreen as GuiChat).typeMessage(config.message)
                (Minecraft.getMinecraft().currentScreen as GuiChat).deactivatedTyping = true
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        (Minecraft.getMinecraft().currentScreen as GuiChat).deactivatedTyping = false
                    }

                },1000)
            }
        }
    }
}