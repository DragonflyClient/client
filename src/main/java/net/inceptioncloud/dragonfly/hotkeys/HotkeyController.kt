package net.inceptioncloud.dragonfly.hotkeys

import net.inceptioncloud.dragonfly.hotkeys.types.HotkeyTypeChat
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Keyboard
import java.awt.Color

object HotkeyController {

    @JvmStatic
    val hotkeys = mutableListOf<Hotkey>()

    init {
        println("Initializing hotkeys...")
        hotkeys.add(
            HotkeyTypeChat(
                Keyboard.KEY_G,
                Keyboard.KEY_LSHIFT,
                1.0,
                10.0,
                Color(0x26de81),
                true,
                "Mitte Safe, alles Safe"
            )
        )
    }

    @JvmStatic
    fun updateKeys() {
        for(hotkey in hotkeys) {
            if(hotkey.modifierKey == null) {
                // Primary key only
            }else {
                if(hotkey.modifierKey == Keyboard.KEY_LSHIFT && GuiScreen.isShiftKeyDown && hotkey.key.isKeyPressed()) {
                    if(hotkey is HotkeyTypeChat) {
                        hotkey.direction = 1
                    }
                }else {
                    if(hotkey is HotkeyTypeChat && !hotkey.transition.isAtStart) {
                        hotkey.direction = -1
                    }
                }
            }
        }
    }

    private fun Int.isKeyPressed(): Boolean = Keyboard.isKeyDown(this)

}