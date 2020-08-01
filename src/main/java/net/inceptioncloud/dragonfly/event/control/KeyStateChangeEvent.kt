package net.inceptioncloud.dragonfly.event.control

import net.inceptioncloud.dragonfly.event.Cancellable
import org.lwjgl.input.Keyboard
import java.awt.image.BufferedImage
import java.io.File

/**
 * Called whenever Minecraft recognizes a key-press.
 *
 * Note that this event is called on every key press regarding of the current gui or state
 * of the game. This also means that cancelling it won't affect cases like movement or gui
 * input but other global key events like Twitch broadcasting and screenshot saving.
 */
data class KeyStateChangeEvent @JvmOverloads constructor(
    val key: Int,
    val press: Boolean = Keyboard.isKeyDown(key)
) : Cancellable()