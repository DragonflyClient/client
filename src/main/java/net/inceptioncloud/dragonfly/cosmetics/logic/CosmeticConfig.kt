package net.inceptioncloud.dragonfly.cosmetics.logic

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.engine.internal.WidgetColor
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

open class CosmeticConfig(val jsonObject: JsonObject) {

    protected fun boolean(defaultValue: Boolean, validator: (Boolean) -> Boolean = { true })
            = BooleanConfigProperty(jsonObject, defaultValue, validator)

    protected fun double(defaultValue: Double, validator: (Double) -> Boolean = { true })
            = DoubleConfigProperty(jsonObject, defaultValue, validator)

    protected fun color(defaultValue: WidgetColor, validator: (WidgetColor) -> Boolean = { true })
            = ColorConfigProperty(jsonObject, defaultValue, validator)
}

abstract class ConfigProperty<T>(
    val jsonObject: JsonObject,
    val defaultValue: T,
    val validator: (T) -> Boolean
) : ReadWriteProperty<CosmeticConfig, T> {

    private var value: T? = null

    override fun setValue(thisRef: CosmeticConfig, property: KProperty<*>, value: T) {
        this.value = value
    }

    override fun getValue(thisRef: CosmeticConfig, property: KProperty<*>): T {
        return value!!
    }

    operator fun provideDelegate(thisRef: CosmeticConfig, property: KProperty<*>): ConfigProperty<T> {
        try {
            if (jsonObject.has(property.name)) {
                val given = convert(jsonObject[property.name])
                if (validator(given)) {
                    value = given
                    return this
                }
            }
        } catch (ignored: Throwable) {}

        value = defaultValue
        return this
    }

    abstract fun convert(element: JsonElement): T
}

class BooleanConfigProperty(
    jsonObject: JsonObject,
    defaultValue: Boolean,
    validator: (Boolean) -> Boolean
) : ConfigProperty<Boolean>(jsonObject, defaultValue, validator) {

    override fun convert(element: JsonElement): Boolean = element.asBoolean
}

class DoubleConfigProperty(
    jsonObject: JsonObject,
    defaultValue: Double,
    validator: (Double) -> Boolean
) : ConfigProperty<Double>(jsonObject, defaultValue, validator) {

    override fun convert(element: JsonElement): Double = element.asDouble
}

class ColorConfigProperty(
    jsonObject: JsonObject,
    defaultValue: WidgetColor,
    validator: (WidgetColor) -> Boolean
) : ConfigProperty<WidgetColor>(jsonObject, defaultValue, validator) {

    override fun convert(element: JsonElement): WidgetColor = Dragonfly.gson.fromJson(element, WidgetColor::class.java)
}