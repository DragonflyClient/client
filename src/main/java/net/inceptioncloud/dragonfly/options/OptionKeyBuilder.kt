package net.inceptioncloud.dragonfly.options

import net.inceptioncloud.dragonfly.apps.settings.DragonflyOptions

class OptionKeyBuilder<T> internal constructor(val typeClass: Class<T>) {

    private var key: String? = null
    private var validator: ((T) -> Boolean)? = null
    private var defaultValue: (() -> T)? = null
    private var optionsBase: OptionsBase = DragonflyOptions

    fun key(key: String?): OptionKeyBuilder<T> {
        this.key = key
        return this
    }

    fun validator(validator: (T) -> Boolean): OptionKeyBuilder<T> {
        this.validator = validator
        return this
    }

    fun defaultValue(defaultValue: () -> T): OptionKeyBuilder<T> {
        this.defaultValue = defaultValue
        return this
    }

    fun defaultValue(defaultValue: T): OptionKeyBuilder<T> {
        this.defaultValue = { defaultValue }
        return this
    }

    fun optionsBase(optionsBase: OptionsBase): OptionKeyBuilder<T> {
        this.optionsBase = optionsBase
        return this
    }

    fun build(): OptionKey<T> {
        return OptionKey(typeClass, key!!, validator!!, defaultValue!!, optionsBase)
    }

}