package net.inceptioncloud.minecraftmod.engine.font

import net.inceptioncloud.minecraftmod.engine.font.renderer.GlyphFontRenderer

/**
 * Represents any font that is loaded and can be rendered in the UI by
 * creating a font renderer.
 *
 * @param name the general name and identifier of the font
 * @param regular the name of the regular font
 * @param light the name of the light version
 * @param medium the name of the medium version
 * @param letterSpacing a custom letter spacing for the font, note that this can be overridden
 * when building a font renderer
 */
class WidgetFont @JvmOverloads constructor(
    val name: String,
    regular: String = name,
    light: String = name,
    medium: String = name,
    private val letterSpacing: Double = 0.0
) {
    /**
     * A simple map containing the names for the different font weights.
     */
    private val fontWeights = mapOf(
        FontWeight.LIGHT to light,
        FontWeight.REGULAR to regular,
        FontWeight.MEDIUM to medium
    )

    /**
     * Cache with already created font renderers stored with their preferences.
     */
    private val cachedFontRenderer = mutableMapOf<FontRendererBuilder, GlyphFontRenderer>()

    /**
     * Creates a font renderer based on this font. You can optionally specify more preferences
     * with the [FontRendererBuilder].
     */
    fun fontRenderer(building: (FontRendererBuilder.() -> Unit)? = null): GlyphFontRenderer {
        val builder = FontRendererBuilder(letterSpacing = letterSpacing)
        building?.invoke(builder)

        return if (builder.forceCreation || builder !in cachedFontRenderer) {
            GlyphFontRenderer.create(
                fontWeights[builder.fontWeight],
                builder.size,
                builder.letterSpacing,
                true,
                true,
                true
            ).also { cachedFontRenderer[builder] = it }
        } else cachedFontRenderer[builder]!!
    }
}