package net.inceptioncloud.dragonfly.event.client

import net.inceptioncloud.dragonfly.event.Cancellable
import java.awt.image.BufferedImage
import java.io.File

/**
 * Called whenever the game window is resized.
 *
 * Contains the native [width] and [height] of the window as well as the [scaledWidth] and [scaledHeight]
 * according to the gui size selected by the user.
 */
data class ResizeEvent(
    val width: Int,
    val height: Int,
    val scaledWidth: Int,
    val scaledHeight: Int
) : Cancellable()