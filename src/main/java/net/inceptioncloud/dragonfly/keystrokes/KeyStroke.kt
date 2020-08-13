package net.inceptioncloud.dragonfly.keystrokes

import org.lwjgl.input.Keyboard

class KeyStroke(val keyCode: Int) {

    var pressed: Boolean = false

    fun switch() {
        pressed = !pressed
    }

    fun isPressed() = keyCode.isKeyPressed();

    private fun Int.isKeyPressed() = Keyboard.isKeyDown(this)

}