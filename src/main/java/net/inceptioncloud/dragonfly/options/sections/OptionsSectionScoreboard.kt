@file:Suppress("MemberVisibilityCanBePrivate")

package net.inceptioncloud.dragonfly.options.sections

import net.inceptioncloud.dragonfly.options.entries.factories.OptionEntryBooleanFactory.Companion.optionEntryBoolean
import net.inceptioncloud.dragonfly.options.entries.factories.OptionEntryMultipleChoiceFactory.Companion.optionEntryMultipleChoice
import net.inceptioncloud.dragonfly.options.entries.util.OptionChoice
import net.inceptioncloud.dragonfly.options.sections.OptionSectionFactory.Companion.optionSection

/**
 * The "Scoreboard" options section.
 *
 * This object contains all options that affect the scoreboard.
 */
object OptionsSectionScoreboard {
    /**
     * ## Title
     * Whether the title of the scoreboard should be visible.
     */
    @JvmStatic
    val scoreboardTitle =
        optionEntryBoolean {
            name = "Title"
            description = "Shows the scoreboard title (first line) of the scoreboard. It usually contains " +
                    "the name of the server or gamemode that you are currently playing."
            key {
                fileKey = "scoreboardTitle"
                default = { true }
            }
        }

    /**
     * ## Background
     * Whether the scoreboard background should be visible.
     */
    @JvmStatic
    val scoreboardBackground =
        optionEntryBoolean {
            name = "Background"
            description = "Renders a transparent black background behind the scoreboard entries and title."
            key {
                fileKey = "scoreboardBackground"
                default = { true }
            }
        }

    /**
     * ## Background
     * Sets the logic which decides whether the scoreboard scores should be rendered.
     * - 0 = off
     * - 1 = on
     * - 2 = auto
     */
    @JvmStatic
    val scoreboardScores = optionEntryMultipleChoice {
        name = "Scores"
        description = "Select the mode in which the scoreboard scores (red numbers on the right) " +
                "should be displayed. (0 = off, 1 = on, 2 = auto)"
        +OptionChoice(0, "Off")
        +OptionChoice(1, "On")
        +OptionChoice(2, "Auto")
        key {
            fileKey = "scoreboardScores"
            default = { 2 }
        }
    }

    /**
     * The init block creates the option section and adds all elements to it.
     */
    @JvmStatic
    fun init() {
        optionSection {
            title = "Scoreboard"

            +scoreboardTitle
            +scoreboardBackground
            +scoreboardScores
        }
    }
}
