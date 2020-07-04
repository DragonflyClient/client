package net.inceptioncloud.dragonfly.engine.sequence.easing

import kotlin.math.pow

/**
 * ## Ease Quart
 *
 * Provides easing functions using the exponent 4.
 */
object EaseQuart
{
    /**
     * ## Ease In Quart
     * Calculates the easing the following way:
     * ```
     * x * x * x * x
     * ```
     *
     * [Visit on easings.net](https://easings.net/#easeInQuart)
     */
    @JvmStatic
    val IN: (Double) -> Double = { it * it * it * it }

    /**
     * ## Ease Out Quart
     * Calculates the easing the following way:
     * ```
     * 1 - pow(1 - x, 4)
     * ```
     *
     * [Visit on easings.net](https://easings.net/#easeOutQuart)
     */
    @JvmStatic
    val OUT: (Double) -> Double = { 1 - (1 - it).pow(4) }

    /**
     * ## Ease In Out Quart
     * Calculates the easing the following way:
     * ```
     * x < 0.5 ? 8 * x * x * x * x : 1 - pow(-2 * x + 2, 4) / 2
     * ```
     *
     * [Visit on easings.net](https://easings.net/#easeInOutQuart)
     */
    @JvmStatic
    val IN_OUT: (Double) -> Double = { if (it < 0.5) 8 * it * it * it * it else 1 - (-2 * it + 2).pow(4) / 2 }
}