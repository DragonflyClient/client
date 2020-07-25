package net.inceptioncloud.dragonfly.options.entries.factories

import net.inceptioncloud.dragonfly.options.OptionKey
import net.inceptioncloud.dragonfly.options.entries.OptionEntryRangeDouble
import net.inceptioncloud.dragonfly.ui.screens.ModOptionsUI

/**
 * A factory class for the [OptionEntryRangeDouble].
 *
 * It extends the [OptionEntryFactory] by adding the [minValue] and [maxValue] and automatically
 * builds the [OptionEntryFactory.KeyFactory.validator] to match the min- and max-value.
 */
class OptionEntryRangeDoubleFactory : OptionEntryFactory<Double>()
{
    /**
     * The smallest value that the range can have.
     *
     * This is the lower bound of the range and used as the left bound on the slider.
     * It is included in the range.
     */
    var minValue: Double? = null

    /**
     * The largest value that the range can have.
     *
     * This is the upper bound of the range and used as the right bound on the slider.
     * It is included in the range.
     */
    var maxValue: Double? = null

    /**
     * The formatter that is used to display the current value.
     *
     * This variable can be overwritten to append something like percent to the value.
     * Otherwise the the default formatter is used.
     */
    var formatter: ((Double) -> String)? = null

    /**
     * An external apply function.
     *
     * If this value isn't null, the value of the key won't automatically be set when the
     * entry value is modified. Instead, it will be saved when the "Save and Exit" button
     * in the options screen is pressed.
     *
     * @see ModOptionsUI
     */
    var externalApply: ((Double, OptionKey<Double>) -> Unit)? = null

    /**
     * A companion object that makes the function [optionEntryRangeDouble] available for
     * static access.
     */
    companion object
    {
        /**
         * The initialization function of the [OptionEntryRangeDoubleFactory].
         *
         * This function creates a new instance of the factory and accepts a function as a
         * parameter. In that function, more steps can be made and values can be assigned
         * to build an [OptionEntryRangeDouble].
         */
        @JvmStatic
        fun optionEntryRangeDouble(init: OptionEntryRangeDoubleFactory.() -> Unit): OptionEntryRangeDouble
        {
            val factory = OptionEntryRangeDoubleFactory()
            factory.init()
            return factory.finish()
        }
    }

    /**
     * Finishes the building of the option entry.
     *
     * This method is called after function in [optionEntryRangeDouble] was executed, so
     * you don't need to call it explicitly.
     */
    private fun finish(): OptionEntryRangeDouble
    {
        val key = OptionKey(
                Double::class.java,
                keyFactory.fileKey,
                keyFactory.validator ?: { value -> value in minValue!!..maxValue!! },
                keyFactory.default
        )

        return OptionEntryRangeDouble(
                name ?: "boolean value",
                description ?: "description not set",
                key,
                minValue!!,
                maxValue!!,
                formatter,
                externalApply
        )
    }
}