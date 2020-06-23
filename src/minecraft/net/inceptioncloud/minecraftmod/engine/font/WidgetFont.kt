package net.inceptioncloud.minecraftmod.engine.font

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
    private val cachedFontRenderer = mutableMapOf<FontRendererBuilder, GlyphFontRenderer>()

    /**
     * A cache with all running async productions and already built font renderers.
     */
    private val asyncBuilding = mutableMapOf<FontRendererBuilder, GlyphFontRenderer?>()

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
    fun fontRenderer(preferences: (FontRendererBuilder.() -> Unit)? = null): GlyphFontRenderer {
        val builder = FontRendererBuilder(FontWeight.REGULAR, 19, letterSpacing)
        preferences?.invoke(builder)

        return if (cachedFontRenderer.containsKey(builder)) {
            cachedFontRenderer[builder]!!
        } else {
            GlyphFontRenderer.create(
                fontWeights[builder.fontWeight],
                builder.size,
                builder.letterSpacing,
                true,
                true,
                true
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
    ): GlyphFontRenderer? {
        val builder = FontRendererBuilder(FontWeight.REGULAR, 19, letterSpacing)
        preferences?.invoke(builder)

        // if a cached version is available
        if (asyncBuilding.containsKey(builder)) {
            return asyncBuilding[builder]
        }

        // store 'null' to indicate that a build is running
        asyncBuilding[builder] = null

        // build the font renderer in a new coroutine
        GlobalScope.launch {
            LogManager.getLogger().info(
                "${Thread.currentThread().name} is building font renderer for ${this@WidgetFont.familyName} with $builder"
            )

            val fontRenderer = fontRenderer(preferences)
            asyncBuilding[builder] = fontRenderer
        }

        return null
    }

    override fun toString(): String {
        return "WidgetFont(name='$familyName')"
    }
}