package net.inceptioncloud.dragonfly.event.client

import net.inceptioncloud.dragonfly.event.Cancellable

/**
 * Called whenever the game window is toggled the fullscreen mode
 *
 * Contains the native [width] and [height] of the window as well as the [scaledWidth] and [scaledHeight]
 * according to the gui size selected by the user, and if the window is now in windowed mode or not.
 */
data class ToggleFullscreenEvent(
    val width: Int,
    val height: Int,
    val scaledWidth: Int,
    val scaledHeight: Int,
    val windowed: Boolean
) : Cancellable()