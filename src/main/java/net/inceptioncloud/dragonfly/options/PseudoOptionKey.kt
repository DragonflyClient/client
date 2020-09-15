package net.inceptioncloud.dragonfly.options

import net.inceptioncloud.dragonfly.apps.settings.DragonflyOptions

class PseudoOptionKey<T>(
    typeClass: Class<T>,
    validator: (T) -> Boolean,
    defaultValue: () -> T,
    val getter: () -> T,
    val setter: (T) -> Unit
) : OptionKey<T>(typeClass, "<pseudo option key>", validator, defaultValue, DragonflyOptions) {

    override fun get(): T {
        return getter()
    }

    override fun set(value: T): Boolean {
        val valid = validator(value)
        if (valid) {
            setter(value)
            listeners.forEach { it(value, value) }
        }
        return valid
    }

    companion object {

        inline fun <reified T> new(): PseudoOptionKeyBuilder<T> = PseudoOptionKeyBuilder(T::class.java)
    }
}

class PseudoOptionKeyBuilder<T>(
    private val typeClass: Class<T>
) {
    private var validator: (T) -> Boolean = { true }
    private var defaultValue: () -> T = { getter() }
    private lateinit var getter: () -> T
    private lateinit var setter: (T) -> Unit

    fun validator(validator: (T) -> Boolean) = apply { this.validator = validator }
    fun defaultValue(defaultValue: () -> T) = apply { this.defaultValue = defaultValue }

    fun get(getter: () -> T) = apply { this.getter = getter }
    fun set(setter: (T) -> Unit) = apply { this.setter = setter }

    fun build() = PseudoOptionKey(typeClass, validator, defaultValue, getter, setter)
}