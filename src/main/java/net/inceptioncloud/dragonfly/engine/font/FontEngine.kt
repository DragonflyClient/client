package net.inceptioncloud.dragonfly.engine.font

import net.inceptioncloud.dragonfly.engine.font.renderer.IFontRenderer
import java.awt.Font
import java.awt.font.TextAttribute
import java.util.HashMap

/**
 * The new core of the Dragonfly font renderer
 */
object FontEngine {

    /**
     * The main Dragonfly font
     */
    val mainFont = WidgetFont(familyName = "Rubik", light = "Rubik Light", regular = "Rubik", medium = "Rubik Medium", letterSpacing = 0.0)

    /**
     * The monospace font that is used by Dragonfly
     */
    val monospaceFont = WidgetFont(familyName = "JetBrains Mono", light = "JetBrains Mono", regular = "JetBrains Mono",
        medium = "JetBrains Mono Medium", letterSpacing = 0.0)
}

/**
 * Holds typography presets for different levels of importance.
 *
 * @param size The size of the font
 * @param tracking The tracking value for the font
 * @param weight The font weight
 */
enum class Typography(val size: Int, val tracking: Double, val weight: FontWeight = FontWeight.REGULAR) {

    HEADING_1(40, -0.06, FontWeight.MEDIUM),
    HEADING_2(32, -0.05, FontWeight.MEDIUM),
    LARGE(28, -0.04),
    BASE(24, -0.03),
    SMALL(20, -0.02),
    SMALLEST(18, -0.02);

    /**
     * Builds the font renderer based on the constructor properties.
     */
    fun buildFontRenderer(): IFontRenderer = FontEngine.mainFont.fontRenderer(weight, size * 2, tracking)
}

/**
 * Convenience function for building a font renderer based on a typography preset.
 */
fun font(typography: Typography) = typography.buildFontRenderer()

/**
 * Convenience function for changing the tracking value of a font.
 */
fun Font.withTracking(tracking: Double): Font {
    val attributes: MutableMap<TextAttribute, Double> = HashMap()
    attributes[TextAttribute.TRACKING] = tracking
    return deriveFont(attributes)
}