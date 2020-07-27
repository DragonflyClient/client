package net.inceptioncloud.dragonfly.hotkeys

import java.awt.Color

abstract class Hotkey {

    abstract val key: Int
    abstract val time: Double
    abstract val delay: Double
    abstract val color: Color
    protected abstract fun actionPerformed()
    abstract fun draw()

}