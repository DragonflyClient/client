@file:Suppress("MemberVisibilityCanBePrivate")

package net.inceptioncloud.dragonfly.options.sections

import net.inceptioncloud.dragonfly.options.entries.factories.OptionEntryBooleanFactory.Companion.optionEntryBoolean
import net.inceptioncloud.dragonfly.options.sections.OptionSectionFactory.Companion.optionSection

/**
 * The "Player" options section.
 *
 * This object contains all options for player, it's look and behavior (like animations, nametags, etc.)
 */
object OptionsSectionPlayer {
    /**
     * ## Render Own Name
     * Whether the user's name should be visible to himself.
     */
    @JvmStatic
    val renderOwnName = optionEntryBoolean {
        name = "Render own Name"
        description = "By default, the Minecraft client doesn't render the user's name. " +
                "If you enable this option, your nametag will be rendered and you can see it in the third person view."
        key {
            fileKey = "renderOwnName"
            default = { true }
        }
    }

    /**
     * The init block creates the option section and adds all elements to it.
     */
    @JvmStatic
    fun init() {
        optionSection {
            title = "Player"

            +renderOwnName
        }
    }
}
