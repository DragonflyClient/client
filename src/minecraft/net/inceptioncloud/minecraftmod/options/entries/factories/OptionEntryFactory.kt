package net.inceptioncloud.minecraftmod.options.entries.factories

/**
 * Part of the option entry factory process.
 *
 * This class contains general factory functions that apply on every type of option entry.
 * The parent factory sets the type parameter to the type that it needs (eg. `Boolean` for [OptionEntryBooleanFactory]).
 */
open class OptionEntryFactory<T>
{
    /**
     * The name of the option that this entry belongs to.
     *
     * This value is displayed on the left of the entry when it's rendered in the Mod Options UI. It is **not** the key
     * that is used in the config file to store the value! This value **must** be set as it is null by default.
     */
    var name: String? = null

    /**
     * A short text that describes the purpose of the option that can be changed with the entry.
     *
     * It is displayed when hovering the entry. This value **must** be set as it is null by default.
     */
    var description: String? = null

    /**
     * An instance of the [KeyFactory] class that can be accessed using the [key] method.
     */
    protected val keyFactory = KeyFactory<T>()

    /**
     * A simple data class that holds information about the key that is created.
     *
     * These information can simply be changed by calling
     * ```
     * fileKey = "..."
     * ```
     * in the [key] function.
     */
    class KeyFactory<T>
    {
        /**
         * The key that is used to store the value in the JSON file.
         *
         * **e.g.** `enableScoreboardBackground`
         */
        var fileKey: String? = null

        /**
         * A function that determines whether the input value is valid.
         *
         * By default, this function validates all input value, but it can be overwritten to change that.
         */
        var validator: (T) -> Boolean = { true }

        /**
         * A function that supplies the default value for the key.
         *
         * It is called when the validation of an input value fails or the type of the value changed.
         * This value **must** be set as it is null by default.
         */
        var default: (() -> T)? = null
    }

    /**
     * Allows access on the [keyFactory] instance of the [KeyFactory] class.
     *
     * The parameter is a lower-order-function
     */
    fun key (init: KeyFactory<T>.() -> Unit): KeyFactory<T>
    {
        keyFactory.init()
        return keyFactory
    }
}