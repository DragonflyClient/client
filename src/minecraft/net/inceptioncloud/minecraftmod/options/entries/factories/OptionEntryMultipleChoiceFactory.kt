package net.inceptioncloud.minecraftmod.options.entries.factories

import net.inceptioncloud.minecraftmod.options.OptionKey
import net.inceptioncloud.minecraftmod.options.entries.OptionEntryMultipleChoice
import net.inceptioncloud.minecraftmod.options.entries.util.OptionChoice
import net.inceptioncloud.minecraftmod.ui.ModOptionsUI

/**
 * A factory class for the [OptionEntryMultipleChoice].
 */
class OptionEntryMultipleChoiceFactory : OptionEntryFactory<Int>()
{
    /**
     * The formatter that is used to display the current value.
     *
     * This variable can be overwritten to append something like percent to the value.
     * Otherwise the the default formatter is used.
     */
    var formatter: ((Int) -> String)? = null

    /**
     * An external apply function.
     *
     * If this value isn't null, the value of the key won't automatically be set when the
     * entry value is modified. Instead, it will be saved when the "Save and Exit" button
     * in the options screen is pressed.
     *
     * @see ModOptionsUI
     */
    var externalApply: ((Int, OptionKey<Int>) -> Unit)? = null

    /**
     * A list of all available choices.
     *
     * Choices can be add using the + unary operator. For instance:
     * ```
     * + OptionChoice(0, "name goes here!")
     * + OptionChoice(1, "other name goes here!")
     * ```
     */
    val choices = mutableListOf<OptionChoice>()

    /**
     * The identifier of the default option choice.
     *
     * The int value must be a valid identifier of one of the [choices]. If not, the initialization
     * of the [OptionEntryMultipleChoice] will throw an exception.
     */
    var default: Int? = null

    /**
     * A companion object that makes the function [optionEntryMultipleChoice] available for
     * static access.
     */
    companion object
    {
        /**
         * The initialization function of the [OptionEntryMultipleChoiceFactory].
         *
         * This function creates a new instance of the factory and accepts a function as a
         * parameter. In that function, more steps can be made and values can be assigned
         * to build an [OptionEntryMultipleChoice].
         */
        @JvmStatic
        fun optionEntryMultipleChoice(init: OptionEntryMultipleChoiceFactory.() -> Unit): OptionEntryMultipleChoice
        {
            val factory = OptionEntryMultipleChoiceFactory()
            factory.init()
            return factory.finish()
        }
    }

    /**
     * Finishes the building of the option entry.
     *
     * This method is called after function in [optionEntryMultipleChoice] was executed, so
     * you don't need to call it explicitly.
     */
    private fun finish(): OptionEntryMultipleChoice
    {
        val key = OptionKey(
                Int::class.java,
                keyFactory.fileKey,
                { value -> value in choices.map { it.identifier } },
                { default }
        )

        return OptionEntryMultipleChoice(
                name ?: "boolean value",
                description ?: "description not set",
                key,
                choices,
                default!!,
                externalApply
        )
    }

    /**
     * Makes it possible to add an [OptionChoice] using + before the initialization.
     */
    operator fun OptionChoice.unaryPlus()
    {
        choices.add(this)
    }
}