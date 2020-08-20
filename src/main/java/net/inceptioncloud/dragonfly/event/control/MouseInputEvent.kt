package net.inceptioncloud.dragonfly.event.control

import net.inceptioncloud.dragonfly.event.Cancellable
import org.lwjgl.input.Mouse

data class MouseInputEvent @JvmOverloads constructor (
    val button: Int,
    val press: Boolean = try { Mouse.isButtonDown(button) }catch (e: Exception) { false }
): Cancellable()