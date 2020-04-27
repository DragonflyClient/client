package net.inceptioncloud.minecraftmod.engine.animation.`in`

import net.inceptioncloud.minecraftmod.engine.animation.Animation
import net.inceptioncloud.minecraftmod.engine.internal.Shape2D
import net.inceptioncloud.minecraftmod.engine.sequence.Sequence
import net.inceptioncloud.minecraftmod.engine.sequence.types.DoubleSequence

/**
 * ## Float Animation (Entrance)
 *
 * Makes the parent object rise up/down and increases the opacity from 0% to 100%.
 *
 * @property duration the duration of the animation in ticks
 * @property distance the distance to the original point that can be negative (by default 40.0)
 * @property easing an optional easing function
 */
open class FloatAnimationIn(val duration: Int, val distance: Double = 40.0, val easing: ((Double) -> Double)? = null)
    : Animation()
{
    open val sequence: Sequence<Double> = DoubleSequence(0.0, 1.0, duration)
        .withEasing(easing)
        .withEndHook { finish() }

    override fun applyToShape(scratchpad: Shape2D<*>, base: Shape2D<*>)
    {
        scratchpad.color.alphaDouble = base.color.alphaDouble * sequence.current
        scratchpad.y = base.y + distance - (distance * sequence.current)
    }

    override fun tick()
    {
        if (!running)
            return

        sequence.next()
    }
}