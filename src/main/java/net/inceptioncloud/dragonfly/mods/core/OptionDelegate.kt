package net.inceptioncloud.dragonfly.mods.core

import net.inceptioncloud.dragonfly.options.OptionKey
import net.inceptioncloud.dragonfly.options.OptionsBase
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.jvmErasure

/**
 * A property delegate that provides an easy way to add options to [DragonflyMod]s.
 * The name of the property is used as the [key][OptionKey.key] value and setting / getting
 * the value of the property is delegated to the [optionKey].
 *
 * @param validator [OptionKey.validator]
 * @param defaultValue [OptionKey.defaultValue]
 * @param optionsBase [OptionKey.optionsBase]
 */
class OptionDelegate<T>(
    private val validator: (T) -> Boolean,
    private val defaultValue: () -> T,
    private val optionsBase: OptionsBase
) : ReadWriteProperty<DragonflyMod, T> {

    /**
     * The option key that is created by the delegate
     */
    private lateinit var optionKey: OptionKey<T>

    /**
     * Retrieves the value from the [optionKey] ([OptionKey.get])
     */
    override fun getValue(thisRef: DragonflyMod, property: KProperty<*>): T {
        return optionKey.get()
    }

    /**
     * Sets the value for the [optionKey] ([OptionKey.set])
     */
    override fun setValue(thisRef: DragonflyMod, property: KProperty<*>, value: T) {
        optionKey.set(value)
    }

    /**
     * Called when the delegate is provided to set initialize the [optionKey].
     */
    @Suppress("UNCHECKED_CAST")
    operator fun provideDelegate(thisRef: DragonflyMod, prop: KProperty<*>): OptionDelegate<T> {
        val typeClass = prop.returnType.jvmErasure.java
        optionKey = OptionKey(
            typeClass as? Class<T> ?: error("Invalid property field type!"),
            prop.name,
            validator,
            defaultValue,
            optionsBase
        )
        return this
    }
}