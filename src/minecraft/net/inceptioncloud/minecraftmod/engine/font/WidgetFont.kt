package net.inceptioncloud.minecraftmod.engine.font

/**
 * Represents any font that is loaded and can be rendered in the UI by
 * creating a font renderer.
 */
class WidgetFont @JvmOverloads constructor(
    val name: String,
    light: String = name,
    regular: String = name,
    medium: String = name,
    private val letterSpacing: Double = 0.0
) {
    private val fontWeights = mapOf(
        FontWeight.LIGHT to light,
        FontWeight.REGULAR to regular,
        FontWeight.MEDIUM to medium
    )

    private val cachedFontRenderer = mutableMapOf<FontRendererBuilder, GlyphFontRenderer>()

    fun fontRenderer(building: (FontRendererBuilder.() -> Unit)? = null): GlyphFontRenderer {
        val builder = FontRendererBuilder(FontWeight.MEDIUM, 19, letterSpacing)
        building?.invoke(builder)

        return cachedFontRenderer.getOrDefault(builder, GlyphFontRenderer.create(
            fontWeights[builder.fontWeight],
            builder.size,
            builder.letterSpacing,
            true,
            true,
            true
        ).also { cachedFontRenderer[builder] = it })
    }
}