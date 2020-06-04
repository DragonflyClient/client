package net.inceptioncloud.minecraftmod.engine.sequence.types

import net.inceptioncloud.minecraftmod.engine.sequence.Sequence

class DoubleSequence(from: Double, to: Double, duration: Int) : Sequence<Double>(from, to, duration)
{
    /**
     * A function to interpolate the value.
     *
     * Responsible for providing the transition between the [from] and the [to] value by
     * interpolating it.
     *
     * @param progress the the quotient of the [time] and the [duration] transformed by the [easing] function
     */
    override fun interpolate(progress: Double): Double
    {
        return (to - from) * progress + from
    }
}
