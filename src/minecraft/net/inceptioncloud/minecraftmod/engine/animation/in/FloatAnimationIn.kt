package net.inceptioncloud.minecraftmod.engine.animation.`in`

import net.inceptioncloud.minecraftmod.engine.animation.Animation
import net.inceptioncloud.minecraftmod.engine.internal.Widget
import net.inceptioncloud.minecraftmod.engine.sequence.types.DoubleSequence
import net.inceptioncloud.minecraftmod.engine.structure.IColor
import net.inceptioncloud.minecraftmod.engine.structure.IOutline
import net.inceptioncloud.minecraftmod.engine.structure.IPosition

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
    /**
     * A sequence that provides the transition for the alpha and y values.
     */
    open val sequence = DoubleSequence(0.0, 1.0, duration)
        .withEasing(easing)
        .withEndHook { finish() }

    override fun applyToShape(scratchpad: Widget<*>, base: Widget<*>)
    {
        scratchpad as IColor
        scratchpad as IPosition
        base as IColor
        base as IPosition

        scratchpad.color.alphaDouble = base.color.alphaDouble * sequence.current
        scratchpad.y = base.y + distance - (distance * sequence.current)

        if (scratchpad is IOutline && base is IOutline)
        {
            scratchpad.outlineColor.alphaDouble = base.outlineColor.alphaDouble * sequence.current
        }
    }

    override fun tick()
    {
        if (!running)
            return

        sequence.next()
    }

    override fun isApplicable(widget: Widget<*>): Boolean =
        widget is IColor && widget is IPosition
}