package net.inceptioncloud.minecraftmod.engine.sequence.easing

import kotlin.math.pow

/**
 * ## Ease Cubic
 *
 * Provides easing functions using the exponent 3.
 */
object EaseCubic
{
    /**
     * ## Ease In Cubic
     * Calculates the easing the following way:
     * ```
     * x * x * x
     * ```
     *
     * [Visit on easings.net](https://easings.net/#easeInCubic)
     */
    @JvmStatic
    val IN: (Double) -> Double = { it * it * it }

    /**
     * ## Ease Out Cubic
     * Calculates the easing the following way:
     * ```
     * 1 - pow(1 - x, 3)
     * ```
     *
     * [Visit on easings.net](https://easings.net/#easeOutCubic)
     */
    @JvmStatic
    val OUT: (Double) -> Double = { 1 - (1 - it).pow(3) }

    /**
     * ## Ease In Out Cubic
     * Calculates the easing the following way:
     * ```
     * x < 0.5 ? 4 * x * x * x : 1 - pow(-2 * x + 2, 3) / 2
     * ```
     *
     * [Visit on easings.net](https://easings.net/#easeInOutCubic)
     */
    @JvmStatic
    val IN_OUT: (Double) -> Double = { if (it < 0.5) 4 * it * it * it else 1 - (-2 * it + 2).pow(3) / 2 }
}