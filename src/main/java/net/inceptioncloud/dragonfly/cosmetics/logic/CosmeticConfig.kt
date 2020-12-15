package net.inceptioncloud.dragonfly.cosmetics.logic

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.engine.internal.WidgetColor
import net.inceptioncloud.dragonfly.utils.Keep
import org.apache.logging.log4j.LogManager
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Superclass for all cosmetic configurations. Provides delegates for easily
 * reading properties from the [jsonObject].
 */
@Keep
open class CosmeticConfig(val jsonObject: JsonObject) {

    /**
     * Use a [BooleanConfigProperty] to parse the property from the [jsonObject].
     */
    protected fun boolean(defaultValue: Boolean, validator: (Boolean) -> Boolean = { true })
            = BooleanConfigProperty(jsonObject, defaultValue, validator)

    /**
     * Use a [DoubleConfigProperty] to parse the property from the [jsonObject].
     */
    protected fun double(defaultValue: Double, validator: (Double) -> Boolean = { true })
            = DoubleConfigProperty(jsonObject, defaultValue, validator)

    /**
     * Use a [ColorConfigProperty] to parse the property from the [jsonObject].
     */
    protected fun color(defaultValue: WidgetColor, validator: (WidgetColor) -> Boolean = { true })
            = ColorConfigProperty(jsonObject, defaultValue, validator)
}

/**
 * Delegation class to handle parsing the property from the [jsonObject].
 *
 * The value is parsed only once when the [delegate is provided][provideDelegate].
 * This means that this class supports changing the value manually after the parsing
 * while respecting the passed [validator]. If the parsing fails, the [defaultValue]
 * will be used as the initial value.
 *
 * @param jsonObject The json object from which the property is parsed
 * @param defaultValue The default value that is used if the validation or parsing fails
 * @param validator Validates incoming values
 */
abstract class ConfigProperty<T>(
    val jsonObject: JsonObject,
    val defaultValue: T,
    val validator: (T) -> Boolean
) : ReadWriteProperty<CosmeticConfig, T> {

    /**
     * The current value that this property represents
     */
    private var value: T? = null

    /**
     * The cosmetic configuration that this property is part of.
     */
    var configReference: CosmeticConfig? = null

    override fun setValue(thisRef: CosmeticConfig, property: KProperty<*>, value: T) {
        if (configReference == null) configReference = thisRef

        if (validator(value)) {
            this.value = value
            jsonObject.add(property.name, Dragonfly.gson.toJsonTree(value))
        }
    }

    override fun getValue(thisRef: CosmeticConfig, property: KProperty<*>): T {
        if (configReference == null) configReference = thisRef
        return value!!
    }

    operator fun provideDelegate(thisRef: CosmeticConfig, property: KProperty<*>): ConfigProperty<T> {
        if (configReference == null) configReference = thisRef
        value = defaultValue

        try {
            if (jsonObject.has(property.name)) {
                val given = convert(jsonObject[property.name])
                if (validator(given)) {
                    value = given
                    return this
                }
            }
        } catch (ignored: Throwable) {}

        return this
    }

    /**
     * To be implemented by subclasses to convert the [element] to their specific type
     * using the functions provided by [JsonElement]
     */
    abstract fun convert(element: JsonElement): T
}

/**
 * Config property for holding boolean values
 */
class BooleanConfigProperty(
    jsonObject: JsonObject,
    defaultValue: Boolean,
    validator: (Boolean) -> Boolean
) : ConfigProperty<Boolean>(jsonObject, defaultValue, validator) {

    override fun convert(element: JsonElement): Boolean = element.asBoolean
}

/**
 * Config property for holding double values
 */
class DoubleConfigProperty(
    jsonObject: JsonObject,
    defaultValue: Double,
    validator: (Double) -> Boolean
) : ConfigProperty<Double>(jsonObject, defaultValue, validator) {

    override fun convert(element: JsonElement): Double = element.asDouble
}

/**
 * Config property for holding color values
 */
class ColorConfigProperty(
    jsonObject: JsonObject,
    defaultValue: WidgetColor,
    validator: (WidgetColor) -> Boolean
) : ConfigProperty<WidgetColor>(jsonObject, defaultValue, validator) {

    override fun convert(element: JsonElement): WidgetColor = Dragonfly.gson.fromJson(element, WidgetColor::class.java)
}