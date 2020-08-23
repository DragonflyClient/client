package net.inceptioncloud.dragonfly.options

import net.inceptioncloud.dragonfly.apps.settings.DragonflyOptions

/**
 * Represents a value that is set in the options file for a specific key.
 *
 * @param T The type of the value
 * @param key The key in String-Format under which the value is saved
 * @param validator Validates whether the current value is acceptable
 * @param defaultValue Supplies the default value
 */
class OptionKey<T>(
    val typeClass: Class<T>,
    val key: String,
    val validator: (T) -> Boolean,
    val defaultValue: () -> T,
    val optionsBase: OptionsBase = DragonflyOptions
) {
    /**
     * @see OptionsBase.getValue
     */
    fun get(): T {
        return optionsBase.getValue(this)
    }

    /**
     * @see OptionsBase.setValue
     */
    fun set(value: T): Boolean {
        return optionsBase.setValue(this, value)
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