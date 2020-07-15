package net.inceptioncloud.dragonfly.event.client

import net.inceptioncloud.dragonfly.event.Cancellable
import java.awt.image.BufferedImage
import java.io.File

/**
 * Called when a screenshot is taken by the client. If this event is cancelled, the screenshot
 * will not be saved and no screenshot utilities will be enabled. However, this has no impact
 * on the creation process since the screenshot is already taken when this event is fired.
 *
 * @param image a buffered image representing the screenshot
 * @param file the file in which the screenshot is stored
 */
data class ScreenshotEvent(
    val image: BufferedImage,
    val file: File
) : Cancellable()