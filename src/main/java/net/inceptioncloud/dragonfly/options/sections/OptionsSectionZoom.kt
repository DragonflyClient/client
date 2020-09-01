@file:Suppress("MemberVisibilityCanBePrivate")

package net.inceptioncloud.dragonfly.options.sections

import net.inceptioncloud.dragonfly.options.entries.factories.OptionEntryBooleanFactory.Companion.optionEntryBoolean
import net.inceptioncloud.dragonfly.options.entries.factories.OptionEntryRangeIntFactory.Companion.optionEntryRangeInt
import net.inceptioncloud.dragonfly.options.sections.OptionSectionFactory.Companion.optionSection
import kotlin.math.roundToInt

/**
 * The "Zoom" options section.
 *
 * Allows the customization of the Optifine zoom.
 */
object OptionsSectionZoom {
    /**
     * ## Animation
     * Whether the zoom should be animated.
     */
    @JvmStatic
    val animation = optionEntryBoolean {
        name = "Animation"
        description = "Smoothly changes the field of view when pressing the zoom button."
        key {
            fileKey = "zoomAnimation"
            default = { true }
        }
    }

    /**
     * ## Field of View
     * The percentage of the original FOV when zooming.
     */
    @JvmStatic
    val fieldOfView = optionEntryRangeInt {
        name = "Field of view"
        description = "The value to which the FOV is reduced during the zoom (the percentage of the original value)."
        minValue = 1
        maxValue = 100
        formatter = {
            val round = it.roundToInt()
            "$round%"
        }
        key {
            fileKey = "zoomFieldOfView"
            default = { 40 }
        }
    }

    /**
     * ## Mouse Sensitivity
     * The percentage of the original mouse sens when zooming.
     */
    @JvmStatic
    val mouseSensitivity = optionEntryRangeInt {
        name = "Mouse sensitivity"
        description = "The mouse sensitivity during the zoom (the percentage of the original value)."
        minValue = 1
        maxValue = 100
        formatter = {
            val round = it.roundToInt()
            "$round%"
        }
        key {
            fileKey = "zoomMouseSensitivity"
            default = { 40 }
        }
    }

    /**
     * The init block creates the option section and adds all elements to it.
     */
    @JvmStatic
    fun init() {
        optionSection {
            title = "Zoom"

            +animation
            +fieldOfView
            +mouseSensitivity
        }
    }
}
