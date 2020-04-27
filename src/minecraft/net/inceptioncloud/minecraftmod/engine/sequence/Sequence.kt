package net.inceptioncloud.minecraftmod.engine.sequence

/**
 * ## Sequence
 *
 * Takes a start and an end value and provides a transition between these two values.
 * This concept is called interpolation. Since the sequence class takes a type parameter,
 * sequences can be created for several types including number values, colors, etc.
 *
 * @param T the type of the sequence, can be any object
 */
abstract class Sequence<T>
(
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
    val duration: Int
)
{
    /**
     * Returns `true` if the sequence has completely run through it's lifecycle and
     * is now at the end. This is the case when the [time] is equal to the [duration].
     */
    val isAtEnd: Boolean
        get() = time == duration

    /**
     * The current value of the sequence.
     *
     * Represents the current state of the interpolation. The value is always between [from]
     * and [to] and is proportional to the progress. It is calculated by calling the [interpolate]
     * function.
     */
    var current: T = from

    /**
     * The expired time of the sequence.
     *
     * Represents the amount of time that the sequence has passed. Starts at 0 and meets its
     * maximum when reaching the [duration].
     */
    protected var time: Int = 0

    /**
     * A function to apply easing to the progress.
     *
     * This function can transform the progress of the sequence in order to create easing.
     * For instance, you can create a quadratic ease-in function by mapping the progress
     * with`x -> x * x`.
     */
    protected var easing: ((Double) -> Double)? = null

    /**
     * A hook that is implemented into the sequence and fired when the [time] switches
     * from zero to one.
     */
    protected var startHook: (Sequence<T>.() -> Unit)? = null

    /**
     * A hook that is implemented into the sequence and fired when the [time] switches
     * to the last value (= [duration]).
     */
    protected var endHook: (Sequence<T>.() -> Unit)? = null

    /**
     * A hook that is implemented into the sequence and fired whenever the progress
     * changes. The passed parameter is the progress value (0.0 - 1.0) that hasn't been
     * transformed by the [easing] function.
     */
    protected var progressHook: (Sequence<T>.(progress: Double) -> Unit)? = null

    /**
     * A function to interpolate the value.
     *
     * Responsible for providing the transition between the [from] and the [to] value by
     * interpolating it.
     *
     * @param progress the the quotient of the [time] and the [duration] transformed by the [easing] function
     */
    abstract fun interpolate(progress: Double): T

    /**
     * Steps into the next execution of the sequence.
     *
     * Increments the [time] value by one and calculates a new progress value. After transforming
     * it by invoking the [easing] function, the interpolation will be executed and the value
     * will be stored in the [current] variable.
     */
    fun next()
    {
        if (time == 0)
        {
            // Fire the start hook
            startHook?.invoke(this)
        }

        time = (time + 1).coerceAtLeast(0).coerceAtMost(duration)

        var progress: Double = time / duration.toDouble()

        // Fire the progress hook
        progressHook?.invoke(this, progress)

        progress = easing?.invoke(progress) ?: progress
        current = interpolate(progress)

        if (time == duration)
        {
            current = to
            // Fire the end hook
            endHook?.invoke(this)
        }
    }

    /* --- Building Methods --- */

    /**
     * @see easing
     */
    fun withEasing(function: ((Double) -> Double)?): Sequence<T>
    {
        if (time != 0)
            throw IllegalStateException("The sequence has already begun! Changes to the behavior can no longer be made.")

        this.easing = function
        return this
    }

    /**
     * @see startHook
     */
    fun withStartHook(function: Sequence<T>.() -> Unit): Sequence<T>
    {
        if (time != 0)
            throw IllegalStateException("The sequence has already begun! Changes to the behavior can no longer be made.")

        this.startHook = function
        return this
    }

    /**
     * @see endHook
     */
    fun withEndHook(function: Sequence<T>.() -> Unit): Sequence<T>
    {
        if (time != 0)
            throw IllegalStateException("The sequence has already begun! Changes to the behavior can no longer be made.")

        this.endHook = function
        return this
    }

    /**
     * @see progressHook
     */
    fun withProgressHook(function: Sequence<T>.(Double) -> Unit): Sequence<T>
    {
        if (time != 0)
            throw IllegalStateException("The sequence has already begun! Changes to the behavior can no longer be made.")

        this.progressHook = function
        return this
    }
}