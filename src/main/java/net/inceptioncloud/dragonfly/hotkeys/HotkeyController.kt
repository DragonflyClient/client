package net.inceptioncloud.dragonfly.hotkeys

import net.inceptioncloud.dragonfly.hotkeys.types.HotkeyTypeChat
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Keyboard
import java.awt.Color

object HotkeyController {

    @JvmStatic
    val hotkeys = mutableListOf<Hotkey>()

    private val pressedKeys = HashMap<Hotkey, List<Int>>()

    init {
        println("Initializing hotkeys...")
        val test = HotkeyTypeChat(
            Keyboard.KEY_G,
            Keyboard.KEY_LSHIFT,
            1.0,
            10.0,
            Color(0x26de81),
            true,
            "Mitte Safe, alles Safe"
        )
        hotkeys.add(test)
        pressedKeys.put(test, listOf(-1, -1))
    }

    @JvmStatic
    fun updateKeys() {

        for (hotkey in hotkeys) {
            if (hotkey.modifierKey == null) {
                // Primary key only
            } else {

                val primary = hotkey.key
                val secondary = hotkey.modifierKey

                if (secondary == Keyboard.KEY_LSHIFT && GuiScreen.isShiftKeyDown && !primary.isKeyPressed()) {
                    pressedKeys[hotkey] = listOf(secondary, -1)
                }

                if (secondary == Keyboard.KEY_LSHIFT && GuiScreen.isShiftKeyDown && primary.isKeyPressed()) {
                    val old = pressedKeys[hotkey]
                    pressedKeys[hotkey] = listOf(old!![0], primary)
                }

                if (pressedKeys[hotkey]!![0] == secondary && pressedKeys[hotkey]!![0] == primary) {
                    if (secondary == Keyboard.KEY_LSHIFT && GuiScreen.isShiftKeyDown && primary.isKeyPressed()) {

                        if (hotkey is HotkeyTypeChat) {
                            hotkey.direction = 1
                        }

                    } else {

                        if (hotkey is HotkeyTypeChat && !hotkey.transition.isAtStart) {
                            hotkey.direction = -1
                        }

                    }
                }

            }
        }
    }

    private fun Int.isKeyPressed(): Boolean = Keyboard.isKeyDown(this)

}