@file:Suppress("MemberVisibilityCanBePrivate")

package net.inceptioncloud.dragonfly.options.sections

import net.inceptioncloud.dragonfly.options.entries.factories.OptionEntryBooleanFactory.Companion.optionEntryBoolean
import net.inceptioncloud.dragonfly.options.entries.factories.OptionEntryMultipleChoiceFactory.Companion.optionEntryMultipleChoice
import net.inceptioncloud.dragonfly.options.entries.util.OptionChoice
import net.inceptioncloud.dragonfly.options.sections.OptionSectionFactory.Companion.optionSection
import net.minecraft.client.Minecraft

/**
 * The "Hot Actions" options section.
 */
object OptionsSectionHotActions {

    /**
     * Whether hot actions should be enabled or not
     */
    @JvmStatic
    val enabled = optionEntryBoolean {
        name = "Enabled"
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
    val triggerMode = optionEntryMultipleChoice {
        name = "Trigger mode"
        description = "Whether to use the modern (trigger key + number key) or the legacy (F7 - F10 keys) trigger mode."
        default = 0

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
    val triggerKey = optionEntryMultipleChoice {
        name = "Trigger key"
        description = "Select the trigger key for the modern trigger mode. If the legacy trigger mode is used this option will have no effect."
        default = 0

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
     * The init block creates the option section and adds all elements to it.
     */
    @JvmStatic
    fun init() {
        optionSection {
            title = "Hot Actions"

            +enabled
            +triggerMode
            +triggerKey
        }
    }
}
