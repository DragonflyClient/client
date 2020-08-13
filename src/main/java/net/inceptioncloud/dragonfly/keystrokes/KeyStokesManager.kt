package net.inceptioncloud.dragonfly.keystrokes

object KeyStokesManager {

    lateinit var forward: KeyStroke
    lateinit var backward: KeyStroke
    lateinit var left: KeyStroke
    lateinit var right: KeyStroke
    lateinit var jump: KeyStroke
    lateinit var sprint: KeyStroke

    @JvmStatic
    fun updateKeyStrokes() {
        println("Forward: ${forward.isPressed()}")
        println("Backward: ${backward.isPressed()}")
        println("Left: ${left.isPressed()}")
        println("Right: ${right.isPressed()}")
        println("Jump: ${jump.isPressed()}")
        println("Sprint: ${sprint.isPressed()}")
    }


}