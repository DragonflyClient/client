package net.inceptioncloud.dragonfly.keystrokes

object KeyStrokesManager {

    var keystrokes = mutableListOf<KeyStroke>()

    lateinit var forward: KeyStroke
    lateinit var backward: KeyStroke
    lateinit var left: KeyStroke
    lateinit var right: KeyStroke
    lateinit var jump: KeyStroke
    lateinit var sprint: KeyStroke
    lateinit var attack: KeyStroke
    lateinit var use: KeyStroke

    @JvmStatic
    fun registerKeyStrokes(keyDesc: String, keyCode: Int) {
        val keyStroke = KeyStroke(keyCode, keyDesc)
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