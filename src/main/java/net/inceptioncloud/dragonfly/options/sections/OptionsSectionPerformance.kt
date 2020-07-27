@file:Suppress("MemberVisibilityCanBePrivate")

package net.inceptioncloud.dragonfly.options.sections

import net.inceptioncloud.dragonfly.options.OptionKey
import net.inceptioncloud.dragonfly.options.entries.factories.OptionEntryBooleanFactory.Companion.optionEntryBoolean
import net.inceptioncloud.dragonfly.options.entries.factories.OptionEntryMultipleChoiceFactory.Companion.optionEntryMultipleChoice
import net.inceptioncloud.dragonfly.options.entries.util.OptionChoice
import net.inceptioncloud.dragonfly.options.sections.OptionSectionFactory.Companion.optionSection
import net.minecraft.client.renderer.chunk.ChunkRenderWorker

/**
 * The "Performance" options section.
 *
 * This object contains all options that have an impact on the performance of the client.
 */
object OptionsSectionPerformance
{
    /**
     * ## Chunk Update Speed
     * Updates the speed of the [ChunkRenderWorker] by changing the delay between update invocations.
     */
    @JvmStatic
    val chunkUpdateSpeed = optionEntryMultipleChoice {
        name = "Chunk Update Speed"
        description = "Limits the speed of chunk updates to relieve the client and improve the performance. This means that the " +
                "world around you will not load all at once but build up step by step while moving around."
        +OptionChoice(25, "Slower")
        +OptionChoice(20, "Slow")
        +OptionChoice(15, "Normal")
        +OptionChoice(10, "Fast")
        +OptionChoice(5, "Faster")
        +OptionChoice(0, "Realtime")
        key {
            fileKey = "chunkUpdateSpeed"
            default = { 10 }
        }
        externalApply = { value: Int, key: OptionKey<Int> ->
            key.set(value)
            ChunkRenderWorker.chunkUpdateDelay = ChunkRenderWorker.calculateUpdateDelay()
        }
    }

    /**
     * ## Use Scaled Font Renderers
     * Enables the use of font renderers that apply a scale to adapt the font size of another
     * font renderer.
     */
    @JvmStatic
    val useScaledFontRenderers = optionEntryBoolean {
        name = "Use Scaled Font Renderers"
        description = "Improves the performance by re-using already created font renderers and adapting the font size by " +
                "applying a scale while rendering. Note that this can cause some animations to be less smooth!"
        key {
            fileKey = "useScaledFontRenderers"
            default = { true }
        }
    }

    /**
     * ## Save Glyphs
     * Enables the storing of glyph (images and properties) on the local machine.
     */
    @JvmStatic
    val saveGlyphs = optionEntryBoolean {
        name = "Save Glyphs"
        description = "Saves the glyphs that are created for the font renderers on the local machine. This will improve the " +
                "performance when entering ui screens and costs less than 5 MB storage."
        key {
            fileKey = "saveGlyphs"
            default = { true }
        }
    }

    /**
     * The init block creates the option section and adds all elements to it.
     */
    @JvmStatic
    fun init()
    {
        optionSection {
            title = "Performance"

            +chunkUpdateSpeed
            +useScaledFontRenderers
            +saveGlyphs
        }
    }
}
