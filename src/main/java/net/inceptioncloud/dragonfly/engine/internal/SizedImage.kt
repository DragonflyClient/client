package net.inceptioncloud.dragonfly.engine.internal

/**
 * Represents an [image] with the given [width] and [height] of the image.
 */
data class SizedImage(
    val image: ImageResource,
    val width: Float,
    val height: Float
)
