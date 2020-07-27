package net.inceptioncloud.dragonfly.hotkeys

import net.inceptioncloud.dragonfly.hotkeys.types.HotkeyTypeChat
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Keyboard
import java.awt.Color

object HotkeyController {

    @JvmStatic
    val hotkeys = mutableListOf<Hotkey>()

    val blockedHotkeys = mutableListOf<Hotkey>()

    init {
        println("Initializing hotkeys...")
        val test = HotkeyTypeChat(
            Keyboard.KEY_G,
            null,
            //Keyboard.KEY_LSHIFT,
            1.0,
            10.0,
            Color(0x26de81),
            false,
            "Mitte Safe, alles Safe"
        )
        hotkeys.add(test)
    }

    @JvmStatic
    fun updateKeys() {

        for (hotkey in hotkeys) {

            val primary = hotkey.key
            val secondary = hotkey.modifierKey

            if (secondary == null) {
                if (primary.isKeyPressed()) {
                    if (hotkey is HotkeyTypeChat && !blockedHotkeys.contains(hotkey)) {
                        hotkey.direction = 1
                    }
                } else {
                    if (hotkey is HotkeyTypeChat) {
                        if (!hotkey.transition.isAtStart) {
                            hotkey.direction = -1
                        }
                    }
                }
            }
        }
    }

    private fun Int.isKeyPressed(): Boolean = Keyboard.isKeyDown(this)

}