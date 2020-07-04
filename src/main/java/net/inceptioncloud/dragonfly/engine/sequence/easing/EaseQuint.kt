package net.inceptioncloud.dragonfly.engine.sequence.easing

import kotlin.math.pow

/**
 * ## Ease Quint
 *
 * Provides easing functions using the exponent 5.
 */
object EaseQuint
{
    /**
     * ## Ease In Quint
     * Calculates the easing the following way:
     * ```
     * x * x * x * x * x
     * ```
     *
     * [Visit on easings.net](https://easings.net/#easeInQuint)
     */
    @JvmStatic
    val IN: (Double) -> Double = { it * it * it * it * it }

    /**
     * ## Ease Out Quint
     * Calculates the easing the following way:
     * ```
     * 1 - pow(1 - x, 5)
     * ```
     *
     * [Visit on easings.net](https://easings.net/#easeOutQuint)
     */
    @JvmStatic
    val OUT: (Double) -> Double = { 1 - (1 - it).pow(5) }

    /**
     * ## Ease In Out Quint
     * Calculates the easing the following way:
     * ```
     * x < 0.5 ? 16 * x * x * x * x * x : 1 - pow(-2 * x + 2, 5) / 2
     * ```
     *
     * [Visit on easings.net](https://easings.net/#easeInOutQuint)
     */
    @JvmStatic
    val IN_OUT: (Double) -> Double = { if (it < 0.5) 16 * it * it * it * it * it else 1 - (-2 * it + 2).pow(5) / 2 }
}