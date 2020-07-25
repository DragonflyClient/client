@file:Suppress("MemberVisibilityCanBePrivate")

package net.inceptioncloud.dragonfly.options.sections

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.options.entries.factories.OptionEntryBooleanFactory
import net.inceptioncloud.dragonfly.options.entries.factories.OptionEntryBooleanFactory.Companion.optionEntryBoolean
import net.inceptioncloud.dragonfly.options.entries.factories.OptionEntryRangeDoubleFactory.Companion.optionEntryRangeDouble
import net.inceptioncloud.dragonfly.options.sections.OptionSectionFactory.Companion.optionSection
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.math.roundToInt

/**
 * The "UI" options section.
 *
 * This object contains all options for the client's user interface.
 */
object OptionsSectionUI
{
    /**
     * ## Font Quality
     * The factor that is applied on the original resolution to improve the font quality.
     */
    @JvmStatic
    val fontQuality = optionEntryRangeDouble {
        name = "Font Quality"
        description = "In order to improve the font quality, the glyphs are rendered in a higher resolution and are then " +
                "scaled down. This option specifies the factor that will be applied on the original resolution."
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
            title = "User Interface"

            +fontQuality
            +useScaledFontRenderers
            +saveGlyphs
        }
    }
}
