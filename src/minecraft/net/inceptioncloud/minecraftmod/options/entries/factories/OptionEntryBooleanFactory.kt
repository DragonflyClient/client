package net.inceptioncloud.minecraftmod.options.entries.factories

import net.inceptioncloud.minecraftmod.options.OptionKey
import net.inceptioncloud.minecraftmod.options.entries.OptionEntryBoolean
import net.inceptioncloud.minecraftmod.ui.ModOptionsUI

/**
 * A factory class for the [OptionEntryBoolean].
 *
 * It doesn't extend the [OptionEntryFactory] but specifies the type (`Boolean`).
 */
class OptionEntryBooleanFactory : OptionEntryFactory<Boolean>()
{
    /**
     * An external apply function.
     *
     * If this value isn't null, the value of the key won't automatically be set when the
     * entry value is modified. Instead, it will be saved when the "Save and Exit" button
     * in the options screen is pressed.
     *
     * @see ModOptionsUI
     */
    var externalApply: ((Boolean, OptionKey<Boolean>) -> Unit)? = null

    /**
     * A companion object that makes the function [optionEntryBoolean] available for static access.
     */
    companion object
    {
        /**
         * The initialization function of the [OptionEntryBooleanFactory].
         *
         * This function creates a new instance of the factory and accepts a function as a parameter.
         * In that function, more steps can be made and values can be assigned to build an [OptionEntryBoolean].
         */
        @JvmStatic
        fun optionEntryBoolean(init: OptionEntryBooleanFactory.() -> Unit): OptionEntryBoolean
        {
            val factory = OptionEntryBooleanFactory()
            factory.init()
            return factory.finish()
        }
    }

    /**
     * Finishes the building of the option entry.
     *
     * This method is called after function in [optionEntryBoolean] was executed, so you don't need to call it explicitly.
     */
    private fun finish (): OptionEntryBoolean
    {
        val key = OptionKey(
                Boolean::class.java,
                keyFactory.fileKey,
                keyFactory.validator,
                keyFactory.default
        )
        return OptionEntryBoolean(
                name ?: "boolean value",
                description ?: "description not set",
                key,
                externalApply
        )
    }
}