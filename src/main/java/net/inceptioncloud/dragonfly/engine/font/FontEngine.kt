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

    HEADING_1(72, -0.02),
    HEADING_2(60, -0.01),
    HEADING_3(48, 0.0),
    HEADING_4(36, 0.0),
    BASE(24, 0.0),
    SMALLEST(16, 0.04);

    /**
     * Builds the font renderer based on the constructor properties.
     */
    fun buildFontRenderer(): IFontRenderer = FontEngine.mainFont.fontRenderer(weight, size * 2, tracking, false)
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