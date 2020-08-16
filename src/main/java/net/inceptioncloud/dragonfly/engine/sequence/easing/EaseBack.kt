package net.inceptioncloud.dragonfly.engine.sequence.easing

import kotlin.math.pow

/**
 * ## Ease Back
 *
 * Provides easing functions that go back before starting to increase their value.
 */
object EaseBack {
    /**
     * ## Ease In Back
     * Calculates the easing the following way:
     * ```
     * val c1 = 1.70158
     * val c3 = c1 + 1
     * c3 * it * it * it - c1 * it * it
     * ```
     *
     * [Visit on easings.net](https://easings.net/#easeInBack)
     */
    @JvmStatic
    val IN: (Double) -> Double = {
        val c1 = 1.70158
        val c3 = c1 + 1

        c3 * it * it * it - c1 * it * it
    }

    /**
     * ## Ease Out Back
     * Calculates the easing the following way:
     * ```
     * val c1 = 1.70158
     * val c3 = c1 + 1
     * 1 + c3 * (it - 1).pow(3) + c1 * (it - 1).pow(2)
     * ```
     *
     * [Visit on easings.net](https://easings.net/#easeOutBack)
     */
    @JvmStatic
    val OUT: (Double) -> Double = {
        val c1 = 1.70158
        val c3 = c1 + 1

        1 + c3 * (it - 1).pow(3) + c1 * (it - 1).pow(2)
    }

    /**
     * ## Ease In Out Back
     * Calculates the easing the following way:
     * ```
     * const c1 = 1.70158;
     * const c2 = c1 * 1.525;
     * return x < 0.5
     * ? (pow(2 * x, 2) * ((c2 + 1) * 2 * x - c2)) / 2
     * : (pow(2 * x - 2, 2) * ((c2 + 1) * (x * 2 - 2) + c2) + 2) / 2;
     * ```
     *
     * [Visit on easings.net](https://easings.net/#easeInOutBack)
     */
    @JvmStatic
    val IN_OUT: (Double) -> Double = {
        val c1 = 1.70158
        val c2 = c1 * 1.525

        if (it < 0.5) ((2 * it).pow(2) * ((c2 + 1) * 2 * it - c2)) / 2
        else ((2 * it - 2).pow(2) * ((c2 + 1) * (it * 2 - 2) + c2) + 2) / 2
    }
}