package net.inceptioncloud.dragonfly.engine.sequence.easing

import kotlin.math.pow

/**
 * ## Ease Quad
 *
 * Provides easing functions using the exponent 2.
 */
object EaseQuad {
    /**
     * ## Ease In Quad
     * Calculates the easing the following way:
     * ```
     * x * x
     * ```
     *
     * [Visit on easings.net](https://easings.net/#easeInQuad)
     */
    @JvmStatic
    val IN: (Double) -> Double = { it * it }

    /**
     * ## Ease Out Quad
     * Calculates the easing the following way:
     * ```
     * 1 - (1 - x) * (1 - x)
     * ```
     *
     * [Visit on easings.net](https://easings.net/#easeOutQuad)
     */
    @JvmStatic
    val OUT: (Double) -> Double = { 1 - (1 - it) * (1 - it) }

    /**
     * ## Ease In Out Quad
     * Calculates the easing the following way:
     * ```
     * x < 0.5 ? 2 * x * x : 1 - pow(-2 * x + 2, 2) / 2
     * ```
     *
     * [Visit on easings.net](https://easings.net/#easeInOutQuad)
     */
    @JvmStatic
    val IN_OUT: (Double) -> Double = { if (it < 0.5) 2 * it * it else 1 - (-2 * it + 2).pow(2) / 2 }
}