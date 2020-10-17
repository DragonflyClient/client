package net.inceptioncloud.dragonfly.engine.sequence.types

import net.inceptioncloud.dragonfly.engine.sequence.Sequence

class DoubleSequence(from: Double, to: Double, duration: Long) : Sequence<Double>(from, to, duration) {
    /**
     * A function to interpolate the value.
     *
     * Responsible for providing the transition between the [from] and the [to] value by
     * interpolating it.
     *
     * @param progress the the quotient of the [expiredTime] and the [duration] transformed by the [easing] function
     */
    override fun interpolate(progress: Double): Double {
        return (to - from) * progress + from
    }
}
