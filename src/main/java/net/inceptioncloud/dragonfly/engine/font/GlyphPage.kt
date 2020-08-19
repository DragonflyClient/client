package net.inceptioncloud.dragonfly.engine.font

import com.google.common.hash.Hashing
import com.google.gson.Gson
import net.inceptioncloud.dragonfly.options.sections.OptionsSectionPerformance
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.DynamicTexture
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.lwjgl.opengl.GL11
import java.awt.*
import java.awt.font.FontRenderContext
import java.awt.font.TextAttribute
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.io.File
import java.nio.charset.Charset
import javax.imageio.ImageIO
import kotlin.math.ceil
import kotlin.math.sqrt

class GlyphPage(val font: Font) {

    @JvmField
    var glyphCharacterMap = mutableMapOf<Char, Glyph>()

    var maxFontHeight = -1
        private set

    private var imgSize = 0

    private var bufferedImage: BufferedImage? = null

    private val loadedTexture by lazy { bufferedImage?.let { DynamicTexture(it) } }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val glyphPage = other as GlyphPage
        return EqualsBuilder()
            .append(font, glyphPage.font)
            .isEquals
    }

    override fun hashCode(): Int {
        return HashCodeBuilder(17, 37)
            .append(font)
            .toHashCode()
    }

    fun generateGlyphPage(chars: CharArray) {
        // obtain the current system graphical settings
        val gfxConfig = GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.defaultConfiguration

        // Calculate glyphPageSize
        var maxWidth = -1.0
        var maxHeight = -1.0
        val affineTransform = AffineTransform()
        val fontRenderContext = FontRenderContext(affineTransform, true, true)
        for (ch in chars) {
            val bounds = font.getStringBounds(ch.toString(), fontRenderContext)
            if (maxWidth < bounds.width) maxWidth = bounds.width
            if (maxHeight < bounds.height) maxHeight = bounds.height
        }

        // Leave some additional space
        maxWidth += 2.0
        maxHeight += 2.0
        imgSize = (ceil(ceil(sqrt(maxWidth * maxWidth * chars.size) / maxWidth)
            .coerceAtLeast(ceil(sqrt(maxHeight * maxHeight * chars.size) / maxHeight)) * maxWidth.coerceAtLeast(maxHeight)
        ) * 1.2).toInt() // make sure there is enough space

        val cached = getCachedGlyph()

        @Suppress("UNCHECKED_CAST")
        if (cached != null) {
            try {
                val (cachedImage, cachedProperties) = cached
                bufferedImage = cachedImage
                glyphCharacterMap = cachedProperties
                    .mapKeys { (it.key as String)[0] }
                    .mapValues {
                        val map = it.value as Map<String, Int>
                        @Suppress("MapGetWithNotNullAssertionOperator")
                        Glyph().apply {
                            x = map["x"]!!
                            y = map["y"]!!
                            width = map["width"]!!
                            height = map["height"]!!
                        }
                    }
                    .toMutableMap()
                maxFontHeight = glyphCharacterMap.map { it.value.height }.max()!!
                return
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        bufferedImage = gfxConfig.createCompatibleImage(imgSize, imgSize, BufferedImage.TYPE_INT_ARGB)

        val graphics = bufferedImage!!.createGraphics()

        graphics.font = font
        graphics.color = Color(255, 255, 255, 0)
        graphics.fillRect(0, 0, imgSize, imgSize)
        graphics.color = Color.white

        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON)
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF)
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

        val fontMetrics = graphics.fontMetrics
        var currentCharHeight = 0
        var posX = 0
        var posY = 1

        for (ch in chars) {
            val glyph = Glyph()
            val bounds = fontMetrics.getStringBounds(ch.toString(), graphics)
            glyph.width = bounds.bounds.width + 8 // Leave some additional space
            glyph.height = bounds.height.toInt()
            check(posY + glyph.height < imgSize) { "Not all characters will fit" }

            if (posX + glyph.width >= imgSize) {
                posX = 0
                posY += currentCharHeight
                currentCharHeight = 0
            }

            glyph.x = posX
            glyph.y = posY

            if (glyph.height > maxFontHeight) {
                maxFontHeight = glyph.height
            }
            if (glyph.height > currentCharHeight) {
                currentCharHeight = glyph.height
            }

            graphics.drawString(ch.toString(), posX, posY + fontMetrics.ascent)
            posX += glyph.width + 4
            glyphCharacterMap[ch] = glyph
        }

        graphics.dispose()

        cacheGlyph()
    }

    fun bindTexture() {
        loadedTexture?.let { GlStateManager.bindTexture(it.glTextureId) }
    }

    fun unbindTexture() {
        GlStateManager.bindTexture(0)
    }

    fun drawChar(ch: Char, x: Float, y: Float): Float {
        val glyph = glyphCharacterMap[if (ch == '▏') '|' else ch] ?: return -1F
        val pageX = glyph.x / imgSize.toFloat()
        val pageY = glyph.y / imgSize.toFloat()
        val pageWidth = glyph.width / imgSize.toFloat()
        val pageHeight = glyph.height / imgSize.toFloat()
        val width = glyph.width.toFloat()
        val height = glyph.height.toFloat()
        GL11.glBegin(GL11.GL_TRIANGLES)
        GL11.glTexCoord2f(pageX + pageWidth, pageY)
        GL11.glVertex2f(x + width, y)
        GL11.glTexCoord2f(pageX, pageY)
        GL11.glVertex2f(x, y)
        GL11.glTexCoord2f(pageX, pageY + pageHeight)
        GL11.glVertex2f(x, y + height)
        GL11.glTexCoord2f(pageX, pageY + pageHeight)
        GL11.glVertex2f(x, y + height)
        GL11.glTexCoord2f(pageX + pageWidth, pageY + pageHeight)
        GL11.glVertex2f(x + width, y + height)
        GL11.glTexCoord2f(pageX + pageWidth, pageY)
        GL11.glVertex2f(x + width, y)
        GL11.glEnd()
        return width - 8
    }

    fun getWidth(ch: Char): Float {
        return (if (glyphCharacterMap.containsKey(ch)) glyphCharacterMap[ch]!!.width else 0).toFloat()
    }

    /** the directory in which the glyphs are cached */
    private val glyphsDirectory by lazy {
        File("dragonfly/glyphs/${hash.substring(0, 2)}/").also { it.mkdirs() }
    }

    /**
     * The hash value that uniquely identifies the glyph page and is used for saving and reading it.
     */
    private val hash by lazy {
        with(font) {
            Hashing.sha1().hashString(
                "${name}-${attributes[TextAttribute.TRACKING]}-${style}-${imgSize}-${size}", Charset.defaultCharset()
            ).toString()
        }
    }

    /** the file that caches the glyph image */
    private val glyphImage by lazy { File(glyphsDirectory, "${hash}.png") }

    /** the file that caches the glyph properties */
    private val glyphProperties by lazy { File(glyphsDirectory, "${hash}.json") }

    /**
     * Returns a cached glyph image for the [font].
     */
    private fun getCachedGlyph(): Pair<BufferedImage, HashMap<*, *>>? =
        if (OptionsSectionPerformance.saveGlyphs() == true && glyphImage.exists() && glyphProperties.exists()) {
            ImageIO.read(glyphImage) to Gson().fromJson(glyphProperties.readText(), HashMap::class.java)
        } else null

    /**
     * Saves the glyph [bufferedImage] to a file and associates it with the [font].
     */
    private fun cacheGlyph() {
        if (OptionsSectionPerformance.saveGlyphs() == true) {
            ImageIO.write(bufferedImage!!, "png", glyphImage)
            glyphProperties.writeText(Gson().toJson(glyphCharacterMap))
        }
    }

    override fun toString(): String {
        return "GlyphPage(font=$font, glyphCharacterMap=$glyphCharacterMap, maxFontHeight=$maxFontHeight, imgSize=$imgSize, bufferedImage=$bufferedImage)"
    }

    class Glyph internal constructor() {
        var x = 0
        var y = 0
        var width = 0
        var height = 0

        override fun toString(): String {
            return "{x=${x.toDouble()}, y=${y.toDouble()}, width=${width.toDouble()}, height=${height.toDouble()}}"
        }
    }
}
