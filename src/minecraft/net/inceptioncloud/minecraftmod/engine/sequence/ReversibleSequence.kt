package net.inceptioncloud.minecraftmod.engine.sequence

/**
 * ## Reversible Sequence
 *
 * A modified version of the [Sequence] class that allows stepping backwards.
 *
 * @param T the type of the sequence, can be any object just like with the normal sequence
 * @constructor
 * Passes all provided arguments to the base [Sequence] class.
 *
 * @param from see [Sequence.from] for documentation
 * @param to see [Sequence.to] for documentation
 * @param duration see [Sequence.duration] for documentation
 */
abstract class ReversibleSequence<T>(from: T, to: T, duration: Int) : Sequence<T>(from, to, duration)
{
    /**
     * Steps into the previous execution of the sequence.
     *
     * Decrements the [time] value by one and calculates a new progress value. After transforming
     * it by invoking the [easing] function, the interpolation will be executed and the value
     * will be stored in the [current] variable.
     *
     * *This is has the opposite effect of calling the [next] function.*
     */
    fun previous()
    {
        if (time == duration)
        {
            // Fire the end hook
            endHook?.invoke(this)
        }

        time = (time - 1).coerceAtLeast(0).coerceAtMost(duration)

        var progress: Double = time / duration.toDouble()

        // Fire the progress hook
        progressHook?.invoke(this, progress)

        progress = easing?.invoke(progress) ?: progress
        current = interpolate(progress)

        if (time == 0)
        {
            current = from
            // Fire the start hook
            startHook?.invoke(this)
        }
    }
}