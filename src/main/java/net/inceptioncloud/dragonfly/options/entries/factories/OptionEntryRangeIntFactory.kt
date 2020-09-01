package net.inceptioncloud.dragonfly.options.entries.factories

import net.inceptioncloud.dragonfly.options.OptionKey
import net.inceptioncloud.dragonfly.options.entries.OptionEntryRangeInt

/**
 * A factory class for the [OptionEntryRangeInt].
 *
 * It extends the [OptionEntryFactory] by adding the [minValue] and [maxValue] and automatically builds the
 * [OptionEntryFactory.KeyFactory.validator] to match the min- and max-value.
 */
class OptionEntryRangeIntFactory : OptionEntryFactory<Int>() {
    /**
     * The smallest value that the range can have.
     *
     * This is the lower bound of the range and used as the left bound on the slider.
     * It is included in the range.
     */
    var minValue: Int? = null

    /**
     * The largest value that the range can have.
     *
     * This is the upper bound of the range and used as the right bound on the slider.
     * It is included in the range.
     */
    var maxValue: Int? = null

    /**
     * The formatter that is used to display the current value.
     *
     * This variable can be overwritten to append something like percent to the value.
     * Otherwise the the default formatter is used.
     */
    var formatter: ((Double) -> String)? = null

    /**
     * A companion object that makes the function [optionEntryRangeInt] available for static access.
     */
    companion object {
        /**
         * The initialization function of the [OptionEntryRangeIntFactory].
         *
         * This function creates a new instance of the factory and accepts a function as a parameter.
         * In that function, more steps can be made and values can be assigned to build an [OptionEntryRangeInt].
         */
        @JvmStatic
        fun optionEntryRangeInt(init: OptionEntryRangeIntFactory.() -> Unit): OptionEntryRangeInt {
            val factory = OptionEntryRangeIntFactory()
            factory.init()
            return factory.finish()
        }
    }

    /**
     * Finishes the building of the option entry.
     *
     * This method is called after function in [optionEntryRangeInt] was executed, so you don't need to call it explicitly.
     */
    private fun finish(): OptionEntryRangeInt {
        return OptionEntryRangeInt(
            name ?: "boolean value",
            description ?: "description not set",
            OptionKey(
                Int::class.java,
                keyFactory.fileKey!!,
                keyFactory.validator ?: { value -> value in minValue!!..maxValue!! },
                keyFactory.default!!
            ),
            minValue!!,
            maxValue!!,
            formatter
        )
    }
}