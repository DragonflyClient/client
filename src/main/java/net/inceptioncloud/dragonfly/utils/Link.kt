package net.inceptioncloud.dragonfly.utils

/**
 * Represents a link inside of a text field.
 *
 * @param x The x position of the link inside of the field
 * @param y The y position of the line in which the link is located
 * @param height The height of the line in which the link is located
 * @param text The text that is displayed instead of the link [url]
 * @param url The url which the link links to
 */
data class Link(
    val x: Double,
    val y: Double,
    val height: Double,
    val text: String,
    val url: String
)