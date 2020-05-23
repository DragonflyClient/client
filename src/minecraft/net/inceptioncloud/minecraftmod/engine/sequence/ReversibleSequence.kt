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
