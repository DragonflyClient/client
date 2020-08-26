package net.inceptioncloud.dragonfly.engine.internal

interface FocusHandler {

    fun captureMouseFocus(data: MouseData): Boolean

    fun captureKeyboardFocus(key: Int): Boolean

    fun handleCapturedMousePress(data: MouseData)
}