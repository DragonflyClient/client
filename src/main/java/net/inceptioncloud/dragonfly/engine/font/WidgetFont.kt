package net.inceptioncloud.dragonfly.engine.font

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
     * Creates a font renderer with the given properties ([fontWeight], [size], [letterSpacing]).
     *
     * Note that this will block the thread during the creation. To have the font renderer built
     * asynchronously, consider using the [fontRendererAsync] function which also allows passing
     * a callback as an additional parameter.
     */
    fun fontRenderer(
        fontWeight: FontWeight = FontWeight.REGULAR,
        size: Int = 19,
        letterSpacing: Double? = null
    ): GlyphFontRenderer {
        val builder = FontRendererBuilder(fontWeight, size, letterSpacing ?: this.letterSpacing)

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
     * Creates a font renderer asynchronously using the given properties ([fontWeight], [size],
     * [letterSpacing]).
     *
     * This function will return null while the building process is running and will return the
     * font renderer if it's ready. You can also pass an optional [callback] as a parameter
     * that will be called immediately once the font renderer has been built.
     */
    fun fontRendererAsync(
        fontWeight: FontWeight = FontWeight.REGULAR,
        size: Int = 19,
        letterSpacing: Double? = null,
        callback: ((GlyphFontRenderer) -> Unit)? = null
    ): GlyphFontRenderer? {
        val builder = FontRendererBuilder(fontWeight, size, letterSpacing ?: this.letterSpacing)

        // if a cached version is available
        if (asyncBuilding.containsKey(builder)) {
            return asyncBuilding[builder]
        }

        // store 'null' to indicate that a build is running
        asyncBuilding[builder] = null

        // build the font renderer in a new coroutine
        GlobalScope.launch {
            LogManager.getLogger().debug(
                "${Thread.currentThread().name} is building font renderer for ${this@WidgetFont.familyName} with $builder"
            )

            val fontRenderer = fontRenderer(fontWeight, size, letterSpacing)
            asyncBuilding[builder] = fontRenderer
            callback?.invoke(fontRenderer)
        }

        return null
    }

    override fun toString(): String {
        return "WidgetFont(name='$familyName')"
    }
}