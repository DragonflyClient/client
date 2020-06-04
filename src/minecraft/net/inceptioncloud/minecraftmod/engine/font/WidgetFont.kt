package net.inceptioncloud.minecraftmod.engine.font

/**
 * Represents any font that is loaded and can be rendered in the UI by
 * creating a font renderer.
 *
 * @param name the name of the font (family)
 * @param light name of the light-weight version
 * @param regular name of the regular-weight version
 * @param medium name of the medium-weight version
 * @param letterSpacing optional modification to the letter spacing
 */
class WidgetFont @JvmOverloads constructor(
    val name: String,
    light: String = name,
    regular: String = name,
    medium: String = name,
    private val letterSpacing: Double = 0.0
) {
    /**
     * The [FontWeight] types mapped to the names supplied by the constructor.
     */
    private val fontWeights = mapOf(
        FontWeight.LIGHT to light,
        FontWeight.REGULAR to regular,
        FontWeight.MEDIUM to medium
    )

    /**
     * Cache for already created font renderer.
     */
    private val cachedFontRenderer = mutableMapOf<FontRendererBuilder, GlyphFontRenderer>()

    /**
     * Builds a new font renderer with preferences set by the [building] block.
     */
    fun fontRenderer(building: (FontRendererBuilder.() -> Unit)? = null): GlyphFontRenderer {
        val builder = FontRendererBuilder(FontWeight.REGULAR, 19, letterSpacing)
        building?.invoke(builder)

        return cachedFontRenderer.getOrDefault(
            builder, GlyphFontRenderer.create(
                fontWeights[builder.fontWeight],
                builder.size,
                builder.letterSpacing,
                true,
                true,
                true
            ).also { cachedFontRenderer[builder] = it })
    }
}