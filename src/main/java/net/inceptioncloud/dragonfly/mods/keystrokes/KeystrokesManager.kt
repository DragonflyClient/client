package net.inceptioncloud.dragonfly.mods.keystrokes

import net.inceptioncloud.dragonfly.mc

object KeystrokesManager {

    var keystrokes = mutableListOf<Keystroke>()
    var savedKeybindings = HashMap<Int, String>()

    val targetDescriptions = listOf("key.forward", "key.back", "key.left", "key.right", "key.jump", "key.attack", "key.use")

    fun registerKeystrokes() {
        savedKeybindings.keys
            .filter { savedKeybindings[it] in targetDescriptions }
            .forEach { keystrokes.add(Keystroke(it, savedKeybindings[it]!!)) }
        mc.ingameGUI.initInGameOverlay()
    }

    @JvmStatic
    fun saveKeybindings(keyDesc: String, keyCode: Int) = savedKeybindings.put(keyCode, keyDesc)

}