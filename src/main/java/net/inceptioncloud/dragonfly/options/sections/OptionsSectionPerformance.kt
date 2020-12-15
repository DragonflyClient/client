@file:Suppress("MemberVisibilityCanBePrivate")

package net.inceptioncloud.dragonfly.options.sections

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.engine.font.WidgetFont
import net.inceptioncloud.dragonfly.options.OptionKey
import net.inceptioncloud.dragonfly.options.entries.factories.OptionEntryBooleanFactory.Companion.optionEntryBoolean
import net.inceptioncloud.dragonfly.options.entries.factories.OptionEntryMultipleChoiceFactory.Companion.optionEntryMultipleChoice
import net.inceptioncloud.dragonfly.options.entries.factories.OptionEntryRangeDoubleFactory.Companion.optionEntryRangeDouble
import net.inceptioncloud.dragonfly.options.entries.util.OptionChoice
import net.inceptioncloud.dragonfly.options.sections.OptionSectionFactory.Companion.optionSection
import net.minecraft.client.renderer.chunk.ChunkRenderWorker
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.math.roundToInt

/**
 * The "Performance" options section.
 *
 * This object contains all options that have an impact on the performance of the client.
 */
object OptionsSectionPerformance {
    /**
     * ## Font Quality
     * The factor that is applied on the original resolution to improve the font quality.
     */
    @JvmStatic
    val fontQuality = optionEntryRangeDouble {
        name = "Font quality"
        description = "In order to improve the font quality, the glyphs are rendered in a higher resolution (multiplied by this factor) " +
                "and are then scaled down. Note that a higher quality has a big impact on the performance in the user interface!"
        minValue = 1.0
        maxValue = 4.0
        externalApply = { value, optionKey ->
            optionKey.set(value)
            Dragonfly.reload()
        }
        formatter = {
            val round = (it * 10).roundToInt() / 10.0
            val nf: NumberFormat = NumberFormat.getNumberInstance(Locale.US)
            val df: DecimalFormat = nf as DecimalFormat

            df.applyPattern("0.0")
            df.format(round)
        }
        key {
            fileKey = "fontQuality"
            default = { 1.5 }
        }
    }

    /**
     * ## Chunk Update Speed
     * Updates the speed of the [ChunkRenderWorker] by changing the delay between update invocations.
     */
    @JvmStatic
    val chunkUpdateSpeed = optionEntryMultipleChoice {
        name = "Chunk update speed"
        description = "Limits the speed of chunk updates to relieve the client and improve the performance. This means that the " +
                "world around you will not load all at once but build up step by step while moving around."
        +OptionChoice(50, "Slower")
        +OptionChoice(40, "Slow")
        +OptionChoice(30, "Normal")
        +OptionChoice(20, "Fast")
        +OptionChoice(10, "Faster")
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
        name = "Use scaled font renderers"
        description = "Improves the ui loading time by re-using already created font renderers and adapting the font size by " +
                "applying a scale while rendering until an actual new font renderer has been created."
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
        name = "Save glyphs"
        description = "Saves the glyphs that are created for the font renderers on the local machine. This will improve the " +
                "performance when entering ui screens and costs less than 5 MB storage."
        key {
            fileKey = "saveGlyphs"
            default = { true }
        }
    }

    /**
     * Whether some commonly used font renderers should be preloaded on startup ([WidgetFont.preload])
     */
    @JvmStatic
    val preloadFontRenderers = optionEntryBoolean {
        name = "Preload font renderers"
        description = "If this option is enabled, some commonly used font renderers are preloaded on game startup so they don't have " +
                "to be created lazily by the ui screens."
        key {
            fileKey = "preloadFontRenderers"
            default = { true }
        }
    }

    /**
     * The init block creates the option section and adds all elements to it.
     */
    @JvmStatic
    fun init() {
        optionSection {
            title = "Performance"

            +fontQuality
            +chunkUpdateSpeed
            +useScaledFontRenderers
            +saveGlyphs
            +preloadFontRenderers
        }
    }
}
