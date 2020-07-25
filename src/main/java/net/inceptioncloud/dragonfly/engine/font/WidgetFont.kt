package net.inceptioncloud.dragonfly.engine.font

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.inceptioncloud.dragonfly.engine.font.renderer.*
import net.inceptioncloud.dragonfly.options.sections.OptionsSectionClient
import net.inceptioncloud.dragonfly.options.sections.OptionsSectionUI
import org.apache.logging.log4j.LogManager

/**
 * Represents any font that is loaded and can be rendered in the UI by
 * creating a font renderer.
 *
 * @param familyName the name of the font (family)
 * @param light name of the light-weight version
 * @param regular name of the regular-weight version
 * @param medium name of the medium-weight version
 * @param letterSpacing optional modification to the letter spacing
 */
class WidgetFont @JvmOverloads constructor(
    val familyName: String,
    light: String = familyName,
    regular: String = familyName,
    medium: String = familyName,
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
    val cachedFontRenderer = mutableMapOf<FontRendererBuilder, IFontRenderer>()

    /**
     * A cache with all running async productions and already built font renderers.
     */
    val asyncBuilding = mutableMapOf<FontRendererBuilder, IFontRenderer?>()

    /**
     * Clears both caches when changing the font quality.
     */
    fun clearCache() {
        cachedFontRenderer.clear()
        asyncBuilding.clear()
    }

    /**
     * Builds a new font renderer with preferences set by the [preferences] block.
     */
    fun fontRenderer(preferences: (FontRendererBuilder.() -> Unit)? = null): IFontRenderer {
        val builder = FontRendererBuilder(FontWeight.REGULAR, 19, letterSpacing)
        preferences?.invoke(builder)

        return if (cachedFontRenderer.containsKey(builder)) {
            cachedFontRenderer[builder]!!
        } else {
            val scaled = findScaled(builder)
            if (scaled != null) {
                cachedFontRenderer[builder] = scaled
                return scaled
            }

            GlyphFontRenderer.create(
                fontWeights[builder.fontWeight],
                builder.size,
                builder.letterSpacing
            ).also { cachedFontRenderer[builder] = it }
        }
    }

    /**
     * Orders the asynchronous creation of a font renderer based on this font with the [preferences].
     * While the renderer is in production, this function will return null. After the production, this function
     * will return a cached font renderer according to the [preferences].
     */
    fun fontRendererAsync(
        preferences: (FontRendererBuilder.() -> Unit)? = null
    ): IFontRenderer? {
        val builder = FontRendererBuilder(FontWeight.REGULAR, 19, letterSpacing)
        preferences?.invoke(builder)

        // if a cached version is available
        if (asyncBuilding.containsKey(builder)) {
            return asyncBuilding[builder]
        } else if (cachedFontRenderer.containsKey(builder)) {
            return cachedFontRenderer[builder]
        }

        val scaled = findScaled(builder)
        if (scaled != null) {
            cachedFontRenderer[builder] = scaled
            return scaled
        }

        // store 'null' to indicate that a build is running
        asyncBuilding[builder] = null

        // build the font renderer in a new coroutine
        GlobalScope.launch {
            LogManager.getLogger().debug(
                "${Thread.currentThread().name} is building font renderer for ${this@WidgetFont.familyName} with $builder"
            )

            val fontRenderer = fontRenderer(preferences)
            asyncBuilding.remove(builder)
            cachedFontRenderer[builder] = fontRenderer
        }

        return null
    }

    /**
     * Tries to find and adapt an already existing font renderer to save resources and improve performance.
     * This will return a [ScaledFontRenderer] object which uses the base font renderer while applying a
     * scale to adapt to the target font size.
     */
    private fun findScaled(builder: FontRendererBuilder) =
        if (OptionsSectionUI.useScaledFontRenderers() != true) {
            null
        } else {
            cachedFontRenderer.toList()
                .filter { (other, _) -> other.fontWeight == builder.fontWeight && other.letterSpacing == builder.letterSpacing }
                .sortedBy { (other, _) -> other.size }
                .firstOrNull { (other, _) -> other.size / builder.size.toDouble() in 1.0..2.0 }
                ?.let { (other, base) -> ScaledFontRenderer(base, (other.size / builder.size.toDouble())) }
        }


    override fun toString(): String {
        return "WidgetFont(name='$familyName')"
    }
}