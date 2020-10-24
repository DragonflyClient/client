package net.inceptioncloud.dragonfly.engine.sequence

import net.inceptioncloud.dragonfly.engine.internal.WidgetColor
import net.inceptioncloud.dragonfly.engine.sequence.types.FloatSequence
import net.inceptioncloud.dragonfly.engine.sequence.types.WidgetColorSequence

/**
 * ## Sequence
 *
 * Takes a start and an end value and provides a transition between these two values.
 * This concept is called interpolation. Since the sequence class takes a type parameter,
 * sequences can be created for several types including number values, colors, etc.
 *
 * @param T the type of the sequence, can be any object
 */
abstract class Sequence<T>(
    /**
     * The value that the sequence starts with.
     *
     * It can be accessed in the [interpolate] function to calculate the current value.
     * This value corresponds to interpolate(0.0F).
     */
    val from: T,

    /**
     * The value that the sequence ends with.
     *
     * It can be accessed in the [interpolate] function to calculate the current value.
     * This value corresponds to interpolate(1.0F).
     */
    val to: T,

    /**
     * The duration of the sequence.
     *
     * Given in amount of mod ticks when 200 ticks correspond to one second.
     */
    val duration: Long
) {
    /**
     * Returns `true` if the sequence has completely run through it's lifecycle and
     * is now at the end. This is the case when the [expiredTime] is equal to the [duration].
     */
    val isAtEnd: Boolean
        get() = expiredTime == duration

    /**
     * A function to apply easing to the progress.
     *
     * This function can transform the progress of the sequence in order to create easing.
     * For instance, you can create a quadratic ease-in function by mapping the progress
     * with`x -> x * x`.
     */
    protected var easing: ((Double) -> Double)? = null
        private set

    /**
     * The expired time of the sequence.
     *
     * Represents the amount of time that the sequence has passed. Starts at 0 and meets its
     * maximum when reaching the [duration].
     */
    protected val expiredTime: Long
        get() = (startTime?.let { System.currentTimeMillis() - it })?.coerceIn(0L..duration) ?: 0L

    /**
     * The time when the animation was started.
     */
    private var startTime: Long? = null

    /**
     * The current value of the sequence.
     *
     * Represents the current state of the interpolation. The value is always between [from]
     * and [to] and is proportional to the progress. It is calculated by calling the [interpolate]
     * function.
     */
    val current: T
        get() {
            val progress: Double = (expiredTime / duration.toDouble()).coerceIn(0.0..1.0)
            val eased = easing?.invoke(progress) ?: progress
            return interpolate(eased)
        }

    /**
     * Starts the animation.
     */
    fun start() {
        if (startTime == null) {
            startTime = System.currentTimeMillis()
        }
    }

    /**
     * A function to interpolate the value.
     *
     * Responsible for providing the transition between the [from] and the [to] value by
     * interpolating it.
     *
     * @param progress the the quotient of the [expiredTime] and the [duration] transformed by the [easing] function
     */
    abstract fun interpolate(progress: Double): T

    /* --- Building Methods --- */

    /**
     * @see easing
     */
    fun withEasing(function: ((Double) -> Double)?): Sequence<T> {
        if (expiredTime != 0L)
            throw IllegalStateException("The sequence has already begun! Changes to the behavior can no longer be made.")

        this.easing = function
        return this
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun <T> generateSequence(start: T, end: T, duration: Int): Sequence<T> = when {
            start is WidgetColor && end is WidgetColor -> WidgetColorSequence(start, end as WidgetColor, duration.toLong() * 5) as Sequence<T>
            start is Float && end is Float -> FloatSequence(start, end, duration.toLong() * 5) as Sequence<T>
            start is Float && end is Double -> FloatSequence(start, end.toFloat(), duration.toLong() * 5) as Sequence<T>
            start is Double && end is Float -> FloatSequence(start.toFloat(), end, duration.toLong() * 5) as Sequence<T>
            else -> throw IllegalArgumentException("No sequence found for this type! ($start; $end)")
        }
    }
}