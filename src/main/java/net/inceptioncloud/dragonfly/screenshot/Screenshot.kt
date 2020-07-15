package net.inceptioncloud.dragonfly.screenshot

import java.awt.image.BufferedImage
import java.io.File

/**
 * Represents a screenshot object with the [name], [image] and [file] of the
 * screenshot.
 */
data class Screenshot(
    val name: String,
    val image: BufferedImage,
    val file: File
)