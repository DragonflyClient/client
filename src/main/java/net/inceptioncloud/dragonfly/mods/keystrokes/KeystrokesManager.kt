package net.inceptioncloud.dragonfly.mods.keystrokes

import net.minecraft.client.Minecraft

object KeystrokesManager {

    var keystrokes = mutableListOf<Keystroke>()
    var savedKeybindings = HashMap<Int, String>()

    lateinit var forward: Keystroke
    lateinit var backward: Keystroke
    lateinit var left: Keystroke
    lateinit var right: Keystroke
    lateinit var jump: Keystroke
    lateinit var sprint: Keystroke
    lateinit var attack: Keystroke
    lateinit var use: Keystroke

    fun registerKeystrokes() {
        for(keyCode in savedKeybindings.keys) {
            val keyStroke = Keystroke(keyCode, savedKeybindings[keyCode]!!)
            when (savedKeybindings[keyCode]!!) {
                "key.forward" -> forward = keyStroke
                "key.back" -> backward = keyStroke
                "key.left" -> left = keyStroke
                "key.right" -> right = keyStroke
                "key.jump" -> jump = keyStroke
                "key.sprint" -> sprint = keyStroke
                "key.attack" -> attack = keyStroke
                "key.use" -> use = keyStroke
            }
            keystrokes.add(keyStroke)
        }
        Minecraft.getMinecraft().ingameGUI.initInGameOverlay()
    }

    @JvmStatic
    fun saveKeybindings(keyDesc: String, keyCode: Int) = savedKeybindings.put(keyCode, keyDesc)

}