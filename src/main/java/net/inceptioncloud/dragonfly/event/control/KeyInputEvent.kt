package net.inceptioncloud.dragonfly.event.control

import net.inceptioncloud.dragonfly.event.Cancellable
import org.lwjgl.input.Keyboard

/**
 * Called whenever Minecraft recognizes a key-press.
 *
 * This event is called either when a key is pressed ingame or in a gui, what means that it can interrupt
 * the key-handling process that is individual for the two game states. Unlike [KeyDispatchEvent], this
 * event can cancel any effect that a key input has.
 *
 * @see KeyDispatchEvent
 */
data class KeyInputEvent @JvmOverloads constructor(
    val key: Int,
    val press: Boolean = try {
        Keyboard.isKeyDown(key)
    } catch (e: Exception) {
        false
    }
) : Cancellable()
