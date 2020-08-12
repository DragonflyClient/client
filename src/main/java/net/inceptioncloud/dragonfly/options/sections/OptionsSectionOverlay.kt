@file:Suppress("MemberVisibilityCanBePrivate")

package net.inceptioncloud.dragonfly.options.sections

import net.inceptioncloud.dragonfly.options.entries.factories.OptionEntryBooleanFactory.Companion.optionEntryBoolean
import net.inceptioncloud.dragonfly.options.entries.factories.OptionEntryMultipleChoiceFactory.Companion.optionEntryMultipleChoice
import net.inceptioncloud.dragonfly.options.entries.util.OptionChoice
import net.inceptioncloud.dragonfly.options.sections.OptionSectionFactory.Companion.optionSection

/**
 * The "Hot Actions" options section.
 */
object OptionsSectionOverlay {

    /**
     * Whether hot actions should be enabled or not
     */
    @JvmStatic
    val enableToastMessages = optionEntryBoolean {
        name = "Enable toast messages"
        description = "Toast messages are small notifications that pop up at the bottom of your screen and display short messages. With this " +
                "option, you can enable or disable them."
        key {
            fileKey = "enableToastMessages"
            default = { true }
        }
    }
    /**
     * Whether hot actions should be enabled or not
     */
    @JvmStatic
    val enableHotActions = optionEntryBoolean {
        name = "Enable hot actions"
        description = "Turning this on enables hot actions to appear at the top left corner of your screen. These hot actions are notifications " +
                "that you can interact with by selecting one of the suggested actions using a trigger or the F7 - F10 keys."
        key {
            fileKey = "enableHotActions"
            default = { true }
        }
    }

    /**
     * The trigger mode that is used to select a suggested action
     */
    @JvmStatic
    val hotActionsTriggerMode = optionEntryMultipleChoice {
        name = "Trigger mode"
        description = "Whether to use the modern (trigger key + number key) or the legacy (F7 - F10 keys) trigger mode for hot actions."

        +OptionChoice(0, "Legacy")
        +OptionChoice(1, "Modern")

        key {
            fileKey = "hotActionsTriggerMode"
            default = { 0 }
        }
    }

    /**
     * Which trigger key is used to select an action (used for the modern trigger mode)
     */
    @JvmStatic
    val hotActionsTriggerKey = optionEntryMultipleChoice {
        name = "Trigger key"
        description = "Select the trigger key for the modern trigger mode. If the legacy trigger mode is used this option will have no effect."

        +OptionChoice(0, "Left Control")
        +OptionChoice(1, "Right Control")
        +OptionChoice(2, "Left Menu (Alt)")
        +OptionChoice(3, "Right Menu")
        +OptionChoice(4, "Left Shift")
        +OptionChoice(5, "Right Shift")
        +OptionChoice(6, "Alt Gr")

        key {
            fileKey = "hotActionsTriggerKey"
            default = { 0 }
        }
    }

    /**
     * Whether the ping is shown as number in the tablist or not
     */
    @JvmStatic
    val showPingAsNumber = optionEntryBoolean {
        name = "Show ping as number"
        description = "Turning this on the ping is shown as number in the tablist."
        key {
            fileKey = "showPingAsNumber"
            default = { true }
        }
    }

    /**
     * The init block creates the option section and adds all elements to it.
     */
    @JvmStatic
    fun init() {
        optionSection {
            title = "Overlay"

            +enableToastMessages
            +enableHotActions
            +hotActionsTriggerMode
            +hotActionsTriggerKey
            +showPingAsNumber
        }
    }
}
