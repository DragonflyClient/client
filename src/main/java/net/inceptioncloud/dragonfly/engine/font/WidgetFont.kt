package net.inceptioncloud.dragonfly.engine.font

import kotlinx.coroutines.*
import net.inceptioncloud.dragonfly.Dragonfly.splashScreen
import net.inceptioncloud.dragonfly.engine.font.renderer.*
import net.inceptioncloud.dragonfly.options.sections.OptionsSectionPerformance
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
    val cachedFontRenderer = mutableMapOf<FontRendererFingerprint, IFontRenderer>()

    /**
     * A cache with all running async productions and already built font renderers.
     */
    val asyncBuilding = mutableMapOf<FontRendererFingerprint, IFontRenderer?>()

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
    @JvmOverloads
    fun fontRenderer(
        fontWeight: FontWeight = FontWeight.REGULAR,
        size: Int = 19,
        letterSpacing: Double? = null,
        useScale: Boolean = true
    ): IFontRenderer {
        val fingerprint = FontRendererFingerprint(fontWeight, size, letterSpacing ?: this.letterSpacing, useScale)

        return if (cachedFontRenderer.containsKey(fingerprint) && cachedFontRenderer[fingerprint] !is ScaledFontRenderer) {
            cachedFontRenderer[fingerprint]!!
        } else {
            GlyphFontRenderer.create(
                fontWeights[fingerprint.fontWeight],
                fingerprint.size,
                fingerprint.letterSpacing,
                useScale
            ).also { cachedFontRenderer[fingerprint] = it }
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
        useScale: Boolean = true,
        callback: ((IFontRenderer) -> Unit)? = null
    ): IFontRenderer? {
        val fingerprint = FontRendererFingerprint(fontWeight, size, letterSpacing ?: this.letterSpacing, useScale)

        // if a cached version is available
        if (cachedFontRenderer.containsKey(fingerprint)) {
            val stored = cachedFontRenderer[fingerprint]
            stored?.takeIf { it !is ScaledFontRenderer }?.let { callback?.invoke(it) }
            return stored
        } else if (asyncBuilding.containsKey(fingerprint)) {
            val stored = asyncBuilding[fingerprint]
            stored?.let { callback?.invoke(it) }
            return stored
        }

        val scaled = findScaled(fingerprint)

        // store 'null' to indicate that a build is running
        asyncBuilding[fingerprint] = null

        // build the font renderer in a new coroutine
        GlobalScope.launch(Dispatchers.IO) {
            LogManager.getLogger().debug(
                "${Thread.currentThread().name} is building font renderer for ${this@WidgetFont.familyName} with $fingerprint"
            )

            val fontRenderer = fontRenderer(fontWeight, size, letterSpacing, useScale)
            asyncBuilding.remove(fingerprint)
            cachedFontRenderer[fingerprint] = fontRenderer
            callback?.invoke(fontRenderer)
        }

        if (scaled != null) {
            cachedFontRenderer[fingerprint] = scaled
            return scaled
        }

        return null
    }

    /**
     * Preloads some commonly used font renderers for this font.
     */
    fun preload() {
        splashScreen.update()

        if (OptionsSectionPerformance.preloadFontRenderers() != true)
            return

        fontRenderer(fontWeight = FontWeight.REGULAR, size = 30)
        fontRenderer(fontWeight = FontWeight.MEDIUM, size = 30)
        fontRenderer(fontWeight = FontWeight.LIGHT, size = 30)
    }

    /**
     * Tries to find and adapt an already existing font renderer to save resources and improve performance.
     * This will return a [ScaledFontRenderer] object which uses the base font renderer while applying a
     * scale to adapt to the target font size.
     */
    private fun findScaled(fingerprint: FontRendererFingerprint) =
        if (OptionsSectionPerformance.useScaledFontRenderers() != true) {
            null
        } else {
            cachedFontRenderer.toList()
                .filter { (other, _) -> other.fontWeight == fingerprint.fontWeight && other.letterSpacing == fingerprint.letterSpacing }
                .sortedBy { (other, _) -> other.size }
                .firstOrNull { (other, _) -> other.size / fingerprint.size.toDouble() in 1.0..2.0 }
                ?.let { (other, base) -> ScaledFontRenderer(base, (other.size / fingerprint.size.toDouble())) }
        }


    override fun toString(): String {
        return "WidgetFont(name='$familyName')"
    }
}
