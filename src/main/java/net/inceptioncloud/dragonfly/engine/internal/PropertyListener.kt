package net.inceptioncloud.dragonfly.engine.internal

/**
 * Simple functional interface to handle property changes.
 */
interface PropertyListener<T> {

    /**
     * Called when the value changes from the [old] one to the [new] one.
     */
    fun changed(old: T, new: T)
}