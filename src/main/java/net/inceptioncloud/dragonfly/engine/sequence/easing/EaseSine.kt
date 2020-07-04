package net.inceptioncloud.dragonfly.engine.sequence.easing

import kotlin.math.*

/**
 * ## Ease Sine
 *
 * Provides easing functions using the sine and cosine functions.
 */
object EaseSine
{
    /**
     * ## Ease In Sine
     * Calculates the easing the following way:
     * ```
     * 1 - cos((x * PI) / 2)
     * ```
     *
     * [Visit on easings.net](https://easings.net/#easeInSine)
     */
    @JvmStatic
    val IN: (Double) -> Double = { 1 - cos((it * PI) / 2) }

    /**
     * ## Ease Out Sine
     * Calculates the easing the following way:
     * ```
     * sin((x * PI) / 2)
     * ```
     *
     * [Visit on easings.net](https://easings.net/#easeOutSine)
     */
    @JvmStatic
    val OUT: (Double) -> Double = { sin((it * PI) / 2) }

    /**
     * ## Ease In Out Sine
     * Calculates the easing the following way:
     * ```
     * -(cos(PI * x) - 1) / 2
     * ```
     *
     * [Visit on easings.net](https://easings.net/#easeInOutSine)
     */
    @JvmStatic
    val IN_OUT: (Double) -> Double = { -(cos(PI * it) - 1) / 2 }
}