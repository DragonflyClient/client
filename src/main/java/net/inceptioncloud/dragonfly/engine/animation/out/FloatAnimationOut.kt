package net.inceptioncloud.dragonfly.engine.animation.out

import net.inceptioncloud.dragonfly.engine.animation.`in`.FloatAnimationIn
import net.inceptioncloud.dragonfly.engine.sequence.Sequence
import net.inceptioncloud.dragonfly.engine.sequence.types.DoubleSequence

/**
 * ## Float Animation (Exit)
 *
 * Counterpart to the [FloatAnimationIn].
 *
 * @see FloatAnimationIn
 */
class FloatAnimationOut(duration: Int, distance: Double = 40.0, easing: ((Double) -> Double)? = null)
    : FloatAnimationIn(duration, distance, easing) {
    /**
     * The reversed version of the [FloatAnimationIn] sequence.
     */
    override val sequence: Sequence<Double> = DoubleSequence(1.0, 0.0, duration)
        .withEasing(easing)
        .withEndHook { finish() }

    override fun finish() {
        widget.isVisible = false
        super.finish()
    }
}