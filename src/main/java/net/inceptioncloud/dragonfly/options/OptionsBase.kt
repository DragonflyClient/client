package net.inceptioncloud.dragonfly.options

import com.google.gson.*
import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.Dragonfly.eventBus
import net.inceptioncloud.dragonfly.engine.internal.WidgetColor
import org.apache.logging.log4j.LogManager
import tornadofx.*
import java.io.*
import java.lang.NumberFormatException
import java.lang.reflect.Type

/**
 * This class manages the reading and writing of the options to the specific file.
 */
open class OptionsBase(val optionsFile: File) {

    /**
     * A cache for values of the option keys.
     *
     * Since computing these values via [gson] can sometimes take resources and decreases
     * the performance, the values are cached in this map. The value is cached when it is
     * accessed the first time [get] or when it changes.
     */
    private val valueCache = mutableMapOf<OptionKey<*>, Any?>()

    /**
     * The Gson instance that allows the (de-)serialization of objects.
     */
    val gson: Gson = Dragonfly.gson

    /**
     * The last read content (via [.contentUpdate]) in JSON-Format.
     */
    private var jsonObject: JsonObject? = null

    /**
     * The logger instance that logs messages for this option base instance.
     */
    private val logger = LogManager.getLogger()

    /**
     * Initial Constructor that updates the content when called.
     */
    init {
        contentUpdate()
    }

    /**
     * Reads the content from the options file and stores it.
     */
    fun contentUpdate() {
        try {
            logger.info("Loading options file: ${optionsFile.name}")
            jsonObject = if (!optionsFile.exists()) JsonObject() else JsonParser().parse(optionsFile.reader()).asJsonObject
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Prints the current (and potential modified) [.jsonObject] to the [.OPTIONS_FILE].
     */
    fun contentSave() {
        try {
            if (!optionsFile.exists() && !optionsFile.createNewFile())
                throw IOException("Unable to create options file: ${optionsFile.name}")

            val fw = FileWriter(optionsFile)
            fw.write(gson.toJson(jsonObject))
            fw.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Returns the value for the given Option Key.
     * If the value isn't from the right type or if the validation fails, the default value will be
     * used and also saved in the [.jsonObject].
     *
     * @param optionKey The key for which the value is requested
     * @param <T>       The type of the value
     *
     * @return The saved value or the default value
     */
    fun <T> getValue(optionKey: OptionKey<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (valueCache.containsKey(optionKey)) // check for cached value
            return valueCache[optionKey] as T

        if (jsonObject!!.has(optionKey.key)) {
            try {
                val jsonElement = jsonObject!![optionKey.key]
                val value = gson.fromJson(jsonElement, optionKey.typeClass)
                if (optionKey.validator(value))
                    return value.also { valueCache[optionKey] = it as Any }
            } catch (exception: JsonSyntaxException) {
                if (exception.cause is IllegalStateException || exception.cause is NumberFormatException) {
                    logger.info("Noticed migrated value type for ${optionKey.key}. " +
                            "Default Value of ${optionKey.defaultValue()} restored!")
                } else {
                    exception.printStackTrace()
                }
            } catch (exception: TypeCastException) {
                logger.info("Illegal value for key ${optionKey.key}! Default value restored.")
            }
        }

        val value = optionKey.defaultValue()
        setValue(optionKey, value)
        return value.also { valueCache[optionKey] = it as Any? }
    }

    /**
     * Puts the value for the option key in the [.jsonObject].
     *
     * @param optionKey The key for which the value is set
     * @param value     The value to be set
     * @param <T>       The type of the value
     *
     * @return False if the value is not valid, otherwise true.
     */
    fun <T> setValue(optionKey: OptionKey<T>, value: T): Boolean {
        if (!optionKey.validator(value)) {
            logger.error("Validation failed for value {} with key {} ({})", value, optionKey.key, optionsFile.name)
            return false
        }
        jsonObject!!.add(optionKey.key, gson.toJsonTree(value))
        valueCache[optionKey] = value as Any?
        return true
    }
}