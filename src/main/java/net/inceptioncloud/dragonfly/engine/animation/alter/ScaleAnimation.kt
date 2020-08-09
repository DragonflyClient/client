package net.inceptioncloud.dragonfly.engine.animation.alter

import net.inceptioncloud.dragonfly.engine.animation.Animation
import net.inceptioncloud.dragonfly.engine.internal.Widget
import net.inceptioncloud.dragonfly.engine.sequence.Sequence
import net.inceptioncloud.dragonfly.engine.sequence.types.DoubleSequence

/**
 * ## Morph Animation (Alter)
 *
 * A morph animation provides a smooth transition from one state (or instance) of a widget to another one.
 * It interpolates all dynamic properties and also modifies the base of the widget instead of only the scratchpad widget.
 *
 * @param duration the amount of mod ticks (200 ^= 1s) that the animation should take to finish
 * @param easing an optional easing function
 */
class ScaleAnimation(
    private val targetFactorX: Double = 1.5,
    private val targetFactorY: Double = 1.5,
    val duration: Int = 100,
    val easing: ((Double) -> Double)? = null
) : Animation() {

    private lateinit var sequenceFactorX: Sequence<Double>

    private lateinit var sequenceFactorY: Sequence<Double>

    override fun initAnimation(parent: Widget<*>): Boolean {
        return if (super.initAnimation(parent)) {
            sequenceFactorX = DoubleSequence(widget.scaleFactorX, targetFactorX, duration)
                .withEasing(easing)
                .withEndHook { finish() }

            sequenceFactorY = DoubleSequence(widget.scaleFactorY, targetFactorY, duration)
                .withEasing(easing)

            true
        } else false
    }

    override fun applyToShape(base: Widget<*>) {
        base.scaleFactorX = sequenceFactorX.current
        base.scaleFactorY = sequenceFactorY.current
    }

    override fun tick() {
        if (!running)
            return

        sequenceFactorX.next()
        sequenceFactorY.next()
    }

    override fun isApplicable(widget: Widget<*>) = true
}