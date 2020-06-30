package net.inceptioncloud.dragonfly.options.entries.util

import net.inceptioncloud.dragonfly.options.OptionKey
import net.inceptioncloud.dragonfly.options.entries.OptionEntryMultipleChoice

/**
 * An option choice represents one of multiple selectable selection options in an [OptionEntryMultipleChoice].
 *
 * Since the value is stored in a [OptionKey] with an [Int] type-parameter, every choice needs an individual
 * integer value with which it can be identified. It also has a display string that shortly describes what
 * this selection will do. This string is rendered in the entry when it is selected.
 *
 * @property identifier the value to identify the selection
 * @property displayString the name of the choice that is rendered
 */
data class OptionChoice(val identifier: Int, val displayString: String)