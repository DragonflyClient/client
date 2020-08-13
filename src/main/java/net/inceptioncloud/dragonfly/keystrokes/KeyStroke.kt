package net.inceptioncloud.dragonfly.keystrokes

class KeyStroke(val keyCode: Int) {

    var pressed: Boolean = false

    fun switch() {
        pressed = !pressed
    }

}