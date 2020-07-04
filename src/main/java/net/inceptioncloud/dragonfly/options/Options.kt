package net.inceptioncloud.dragonfly.options

import com.google.gson.*
import net.inceptioncloud.dragonfly.Dragonfly.eventBus
import org.apache.commons.lang3.Validate
import org.apache.logging.log4j.LogManager
import java.io.*

/**
 * This class manages the reading and writing of the options to the specific file.
 */
object Options {

    /**
     * The file in which the options are saved.
     */
    private val OPTIONS_FILE = File("dragonfly/options.json")

    /**
     * The Gson instance that allows the (de-)serialization of objects.
     */
    @JvmStatic
    val gson: Gson

    /**
     * The last read content (via [.contentUpdate]) in JSON-Format.
     */
    private var jsonObject: JsonObject? = null

    /**
     * Reads the content from the options file and stores it.
     */
    @JvmStatic
    fun contentUpdate() {
        try {
            LogManager.getLogger().info("Loading Settings...")
            jsonObject = if (!OPTIONS_FILE.exists()) JsonObject() else JsonParser().parse(FileReader(OPTIONS_FILE)).asJsonObject
            LogManager.getLogger().info(jsonObject)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Prints the current (and potential modified) [.jsonObject] to the [.OPTIONS_FILE].
     */
    @JvmStatic
    fun contentSave() {
        try {
            if (!OPTIONS_FILE.exists() && !OPTIONS_FILE.createNewFile()) throw IOException(
                "Unable to create options.json file!"
            )
            val fw = FileWriter(OPTIONS_FILE)
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
    @JvmStatic
    fun <T> getValue(optionKey: OptionKey<T>): T {
        Validate.notNull(optionKey, "The key for the options value cannot be null!")

        if (jsonObject!!.has(optionKey.key)) {
            try {
                val jsonElement = jsonObject!![optionKey.key]
                val value = gson.fromJson(jsonElement, optionKey.typeClass)
                if (optionKey.validator.test(value)) return value
            } catch (exception: JsonSyntaxException) {
                if (exception.cause!!.javaClass.simpleName == "IllegalStateException" || exception.cause!!.javaClass.simpleName == "NumberFormatException") {
                    LogManager.getLogger().info(
                        "Noticed migrated value type for " + optionKey.key
                                + ". Default Value of " + optionKey.defaultValue.get() + " restored!"
                    )
                } else {
                    exception.printStackTrace()
                }
            }
        }

        val value = optionKey.defaultValue.get()
        setValue(optionKey, value)
        return value
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
    @JvmStatic
    fun <T> setValue(optionKey: OptionKey<T>, value: T): Boolean {
        if (!optionKey.validator.test(value)) {
            LogManager.getLogger()
                .error("Failed to set option value {} for key {} (validation failed!)", value, optionKey.key)
            return false
        }
        jsonObject!!.add(optionKey.key, gson.toJsonTree(value))
        return true
    }

    /**
     * Initial Constructor that updates the content when called.
     */
    init {
        eventBus.register(OptionSaveSubscriber())
        gson = GsonBuilder().setPrettyPrinting().create()
        contentUpdate()
    }
}