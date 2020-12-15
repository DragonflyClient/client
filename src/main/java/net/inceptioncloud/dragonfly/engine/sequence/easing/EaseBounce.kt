package net.inceptioncloud.dragonfly.engine.sequence.easing

object EaseBounce {
    /**
     * ## Ease In Bounce
     * Calculates the easing the following way:
     * ```
     * 1 - easeOutBounce(1 - x)
     * ```
     *
     * [Visit on easings.net](https://easings.net/#easeInBounce)
     */
    @JvmStatic
    val IN: (Double) -> Double = { 1 - OUT.invoke(1 - it) }

    /**
     * ## Ease Out Bounce
     * Calculates the easing the following way:
     * ```
     * const n1 = 7.5625;
     * const d1 = 2.75;
     *
     * if (x < 1 / d1) {
     * return n1 * x * x;
     * } else if (x < 2 / d1) {
     * return n1 * (x -= 1.5 / d1) * x + 0.75;
     * } else if (x < 2.5 / d1) {
     * return n1 * (x -= 2.25 / d1) * x + 0.9375;
     * } else {
     * return n1 * (x -= 2.625 / d1) * x + 0.984375;
     * }
     * ```
     *
     * [Visit on easings.net](https://easings.net/#easeOutBounce)
     */
    @JvmStatic
    val OUT: (Double) -> Double = {
        val n1 = 7.5625
        val d1 = 2.75
        var x = it

        when {
            x < 1 / d1 -> {
                n1 * x * x
            }
            x < 2 / d1 -> {
                x -= 1.5 / d1
                n1 * it * it + 0.75
            }
            x < 2.5 / d1 -> {
                x -= 2.25 / d1
                n1 * x * x + 0.9375
            }
            else -> {
                x -= 2.625 / d1
                n1 * x * x + 0.984375
            }
        }
    }

    /**
     * ## Ease In Out Bounce
     * Calculates the easing the following way:
     * ```
     * return x < 0.5
    ? (1 - easeOutBounce(1 - 2 * x)) / 2
    : (1 + easeOutBounce(2 * x - 1)) / 2;
     * ```
     *
     * [Visit on easings.net](https://easings.net/#easeInOutBounce)
     */
    @JvmStatic
    val IN_OUT: (Double) -> Double = {
        if (it < 0.5) (1 - OUT.invoke(1 - 2 * it)) / 2
        else (1 + OUT.invoke(2 * it - 1)) / 2
    }
}