package net.inceptioncloud.dragonfly.options.sections

import net.inceptioncloud.dragonfly.options.entries.factories.OptionEntryBooleanFactory
import net.inceptioncloud.dragonfly.options.entries.factories.OptionEntryBooleanFactory.Companion.optionEntryBoolean
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
        name = "Message restore mode"
        description = "When closing the chat, your last entered message (that hasn't been sent) can be restored. " +
                "You can select when this should happen."

        +OptionChoice(0, "Never")
        +OptionChoice(1, "Force-Close")
        +OptionChoice(2, "Always")

        key {
            fileKey = "messageRestoreMode"
            default = { 1 }
        }
    }

    /**
     * Whether empty chat messages should not be displayed in the chat.
     */
    @JvmStatic
    val ignoreEmptyChatMessages = optionEntryBoolean {
        name = "Ignore empty chat messages"
        description = "When receiving chat messages from the server that have no content, these messages are not " +
                "displayed in the chat."

        key {
            fileKey = "ignoreEmptyChatMessages"
            default = { false }
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
            +ignoreEmptyChatMessages
        }
    }
}