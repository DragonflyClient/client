package net.inceptioncloud.dragonfly.options.sections

import net.inceptioncloud.dragonfly.options.entries.factories.OptionEntryMultipleChoiceFactory.Companion.optionEntryMultipleChoice
import net.inceptioncloud.dragonfly.options.entries.util.OptionChoice
import net.inceptioncloud.dragonfly.options.sections.OptionSectionFactory.Companion.optionSection

/**
 * The "Chat" options section.
 *
 * This object contains all options that affect the chat and its behavior.
 */
object OptionsSectionChat {

    /**
     * ## Message Restore Mode
     * Specifies the behavior of the message-restore feature in the chat.
     */
    @JvmStatic
    val messageRestoreMode = optionEntryMultipleChoice {
        name = "Message Restore Mode"
        description = "When closing the chat, your last entered message (that hasn't been sent) can be restored. " +
                "You can select when this should happen."
        default = 1

        +OptionChoice(0, "Never")
        +OptionChoice(1, "Force-Close")
        +OptionChoice(2, "Always")

        key {
            fileKey = "messageRestoreMode"
            default = { 1 }
        }
    }

    /**
     * The init block creates the option section and adds all elements to it.
     */
    @JvmStatic
    fun init() {
        optionSection {
            title = "Chat"

            +messageRestoreMode
        }
    }
}