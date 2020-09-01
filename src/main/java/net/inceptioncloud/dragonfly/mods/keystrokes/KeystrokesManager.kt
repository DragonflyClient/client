package net.inceptioncloud.dragonfly.mods.keystrokes

object KeystrokesManager {

    var keystrokes = mutableListOf<Keystroke>()

    lateinit var forward: Keystroke
    lateinit var backward: Keystroke
    lateinit var left: Keystroke
    lateinit var right: Keystroke
    lateinit var jump: Keystroke
    lateinit var sprint: Keystroke
    lateinit var attack: Keystroke
    lateinit var use: Keystroke

    /**
     * Function which is used to register a new Keystroke
     */
    @JvmStatic
    fun registerKeystrokes(keyDesc: String, keyCode: Int) {
        val keyStroke = Keystroke(keyCode, keyDesc)
        when (keyDesc) {
            "key.forward" -> forward = keyStroke
            "key.back" -> backward = keyStroke
            "key.left" -> left = keyStroke
            "key.right" -> right = keyStroke
            "key.jump" -> jump = keyStroke
            "key.sprint" -> sprint = keyStroke
            "key.attack" -> attack = keyStroke
            "key.use" -> use = keyStroke
            else -> return
        }
        keystrokes.add(keyStroke)

    }

}