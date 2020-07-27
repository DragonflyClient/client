package net.inceptioncloud.dragonfly.hotkeys

import net.inceptioncloud.dragonfly.hotkeys.types.HotkeyTypeChat
import org.lwjgl.input.Keyboard
import java.awt.Color

class HotkeyController {

    var hotkeys = mutableListOf<Hotkey>()

    init {
        println("Initializing hotkeys...")
        hotkeys.add(HotkeyTypeChat(Keyboard.KEY_G, 1.0, 10.0, Color(0x26de81), true, "Mitte Safe, alles Safe"))
    }

}