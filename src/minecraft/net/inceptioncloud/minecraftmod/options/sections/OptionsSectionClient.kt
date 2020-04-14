@file:Suppress("MemberVisibilityCanBePrivate")

package net.inceptioncloud.minecraftmod.options.sections

import net.inceptioncloud.minecraftmod.options.entries.factories.OptionEntryBooleanFactory.Companion.optionEntryBoolean
import net.inceptioncloud.minecraftmod.options.sections.OptionSectionFactory.Companion.optionSection
import net.minecraft.client.Minecraft

/**
 * The "Client" options section.
 *
 * This object contains all options for the client itself, like the window or the program behaviour.
 */
object OptionsSectionClient
{
    /**
     * ## Windowed Fullscreen
     * If this is enabled, the user can use the cursor outside the client window when it is visible (thus in a GUI).
     * Note that this can cause lack in performance.
     */
    @JvmStatic
    val windowedFullscreen = optionEntryBoolean {
        name = "Windowed Fullscreen"
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
     * The init block creates the option section and adds all elements to it.
     */
    @JvmStatic
    fun init()
    {
        optionSection {
            title = "Client"

            +windowedFullscreen
        }
    }
}
