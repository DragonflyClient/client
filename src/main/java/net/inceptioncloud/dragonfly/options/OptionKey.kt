package net.inceptioncloud.dragonfly.options

import javafx.beans.property.SimpleObjectProperty
import net.inceptioncloud.dragonfly.apps.settings.DragonflyOptions

/**
 * Represents a value that is set in the options file for a specific key.
 *
 * @param T The type of the value
 * @param key The key in String-Format under which the value is saved
 * @param validator Validates whether the current value is acceptable
 * @param defaultValue Supplies the default value
 * @param optionsBase The options base instance to which the key's values are saved.
 */
class OptionKey<T>(
    val typeClass: Class<T>,
    val key: String,
    val validator: (T) -> Boolean,
    val defaultValue: () -> T,
    val optionsBase: OptionsBase = DragonflyOptions
) {

    /**
     * A simple object property that can be used to observe the option key but
     * has no influence on the value.
     */
    val objectProperty = SimpleObjectProperty<T>(optionsBase.getValue(this))

    /**
     * @see OptionsBase.getValue
     */
    fun get(): T {
        val value = optionsBase.getValue(this)
        objectProperty.get()
        return value
    }

    /**
     * @see OptionsBase.setValue
     */
    fun set(value: T): Boolean {
        val success = optionsBase.setValue(this, value)
        objectProperty.set(value)
        return success
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