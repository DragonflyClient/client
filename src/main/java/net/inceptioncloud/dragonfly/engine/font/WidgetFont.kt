package net.inceptioncloud.dragonfly.engine.font

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.inceptioncloud.dragonfly.engine.font.renderer.*
import net.inceptioncloud.dragonfly.options.sections.*
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
    ): IFontRenderer {
        val builder = FontRendererBuilder(fontWeight, size, letterSpacing ?: this.letterSpacing)

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
     * Creates a font renderer asynchronously using the given properties ([fontWeight], [size],
     * [letterSpacing]).
     *
     * This function will return null while the building process is running and will return the
     * font renderer if it's ready. You can also pass an optional [callback] as a parameter
     * that will be called immediately once the font renderer has been built and if it is already
     * available.
     */
    fun fontRendererAsync(
        fontWeight: FontWeight = FontWeight.REGULAR,
        size: Int = 19,
        letterSpacing: Double? = null,
        callback: ((IFontRenderer) -> Unit)? = null
    ): IFontRenderer? {
        val builder = FontRendererBuilder(fontWeight, size, letterSpacing ?: this.letterSpacing)

        // if a cached version is available
        if (asyncBuilding.containsKey(builder)) {
            val stored = asyncBuilding[builder]
            stored?.let { callback?.invoke(it) }
            return stored
        } else if (cachedFontRenderer.containsKey(builder)) {
            val stored = cachedFontRenderer[builder]
            stored?.let { callback?.invoke(it) }
            return stored
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
            callback?.invoke(fontRenderer)
        }

        return null
    }

    /**
     * Tries to find and adapt an already existing font renderer to save resources and improve performance.
     * This will return a [ScaledFontRenderer] object which uses the base font renderer while applying a
     * scale to adapt to the target font size.
     */
    private fun findScaled(builder: FontRendererBuilder) =
        if (OptionsSectionPerformance.useScaledFontRenderers() != true) {
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