package net.inceptioncloud.dragonfly.options

import net.inceptioncloud.dragonfly.apps.settings.DragonflyOptions

typealias ChangeListener<T> = (oldValue: T, newValue: T) -> Unit

/**
 * Represents a value that is set in the options file for a specific key.
 *
 * @param T The type of the value
 * @param key The key in String-Format under which the value is saved
 * @param validator Validates whether the current value is acceptable
 * @param defaultValue Supplies the default value
 * @param optionsBase The options base instance to which the key's values are saved.
 */
open class OptionKey<T>(
    val typeClass: Class<T>,
    val key: String,
    val validator: (T) -> Boolean,
    val defaultValue: () -> T,
    val optionsBase: OptionsBase = DragonflyOptions
) {
    /**
     * Contains all listeners that have been added to this option key that are notified
     * when its value changes.
     */
    protected val listeners = mutableListOf<ChangeListener<T>>()

    /**
     * @see OptionsBase.getValue
     */
    open fun get(): T {
        return optionsBase.getValue(this)
    }

    /**
     * @see OptionsBase.setValue
     */
    open fun set(value: T): Boolean {
        val success = optionsBase.setValue(this, value)
        listeners.forEach { it(value, value) }
        return success
    }

    /**
     * Returns the default value of the option key.
     */
    open fun getDefaultValue() = defaultValue()

    /**
     * Adds a new [listener] to the option key.
     */
    fun addListener(listener: ChangeListener<T>) {
        listeners.add(listener)
    }

    /**
     * Removes a [listener] from the option key.
     */
    fun removeListener(listener: ChangeListener<T>) {
        listeners.remove(listener)
    }

    companion object {

        /**
         * Returns a new [OptionKeyBuilder] to build a new instance of the [OptionKey] class.
         */
        @JvmStatic
        fun <T> newInstance(typeClass: Class<T>): OptionKeyBuilder<T> {
            return OptionKeyBuilder(typeClass)
        }
    }
}