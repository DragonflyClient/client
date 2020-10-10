package net.inceptioncloud.dragonfly.engine.utils

import net.inceptioncloud.dragonfly.engine.internal.Point
import kotlin.math.cos
import kotlin.math.sin

/**
 * Returns a list of all points around the given center ([x], [y]) with the given [radius]
 * in the section of the given [degrees].
 */
fun getCirclePoints(
    x: Double, y: Double, radius: Double, degrees: IntProgression = 0..360
): List<Point> = degrees.map {
    val rad = Math.toRadians(it.toDouble())
    val offsetX = cos(rad) * radius
    val offsetY = sin(rad) * radius
    Point(x + offsetX, y + offsetY)
}