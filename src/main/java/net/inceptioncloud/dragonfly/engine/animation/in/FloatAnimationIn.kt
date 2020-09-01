package net.inceptioncloud.dragonfly.engine.animation.`in`

import net.inceptioncloud.dragonfly.engine.animation.Animation
import net.inceptioncloud.dragonfly.engine.internal.Widget
import net.inceptioncloud.dragonfly.engine.sequence.types.DoubleSequence
import net.inceptioncloud.dragonfly.engine.structure.*

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
    : Animation() {
    /**
     * A sequence that provides the transition for the alpha and y values.
     */
    open val sequence = DoubleSequence(0.0, 1.0, duration)
        .withEasing(easing)
        .withEndHook { finish() }

    override fun applyToShape(base: Widget<*>) {
        base as IColor
        base as IPosition

        base.color.alphaDouble = base.color.alphaDouble * sequence.current
        base.y = base.y + distance - (distance * sequence.current)

        if (base is IOutline) {
            base.outlineColor.alphaDouble = base.outlineColor.alphaDouble * sequence.current
        }
    }

    override fun tick() {
        if (!running)
            return

        sequence.next()
    }

    override fun isApplicable(widget: Widget<*>): Boolean =
        widget is IColor && widget is IPosition
}