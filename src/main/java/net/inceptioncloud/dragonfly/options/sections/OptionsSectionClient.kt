@file:Suppress("MemberVisibilityCanBePrivate")

package net.inceptioncloud.dragonfly.options.sections

import net.inceptioncloud.dragonfly.options.entries.factories.OptionEntryBooleanFactory.Companion.optionEntryBoolean
import net.inceptioncloud.dragonfly.options.sections.OptionSectionFactory.Companion.optionSection
import net.minecraft.client.Minecraft

/**
 * The "Client" options section.
 *
 * This object contains all options for the client itself, like the window or the program behaviour.
 */
object OptionsSectionClient {

    /**
     * ## Windowed Fullscreen
     * If this is enabled, the user can use the cursor outside the client window when it is visible (thus in a GUI).
     * Note that this can cause lack in performance.
     */
    @JvmStatic
    val windowedFullscreen = optionEntryBoolean {
        name = "Windowed fullscreen"
        description = "Uses windowed fullscreen instead of the default fullscreen to allow moving " +
                "the cursor out of the game when in a GUI."
        externalApply = { value, optionKey ->
            optionKey.set(value)
            Minecraft.getMinecraft().toggleFullscreen()
            Minecraft.getMinecraft().toggleFullscreen()
        }
        key {
            fileKey = "windowedFullscreen"
            default = { false }
        }
    }

    /**
     * ## Screenshot Utilities
     *
     * Enables uploading and copying of screenshots directly from within the client.
     */
    @JvmStatic
    val screenshotUtilities = optionEntryBoolean {
        name = "Screenshot utilities"
        description = "Provides additional utility functions to the screenshot system like uploading and " +
                "copying. Note that this overrides the default screenshot behavior."
        key {
            fileKey = "screenshotUtilities"
            default = { true }
        }
    }

    /**
     * The init block creates the option section and adds all elements to it.
     */
    @JvmStatic
    fun init() {
        optionSection {
            title = "Client"

            +windowedFullscreen
            +screenshotUtilities
        }
    }
}
