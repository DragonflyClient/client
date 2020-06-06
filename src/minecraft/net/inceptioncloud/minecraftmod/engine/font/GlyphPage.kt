package net.inceptioncloud.minecraftmod.engine.font

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.DynamicTexture
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.font.FontRenderContext
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.util.*
import kotlin.math.ceil
import kotlin.math.sqrt

class GlyphPage(
    val font: Font,
    private val isAntiAliasingEnabled: Boolean,
    private val isFractionalMetricsEnabled: Boolean
) {

    @JvmField
    var glyphCharacterMap = HashMap<Char, Glyph>()
    private var imgSize = 0
    var maxFontHeight = -1
        private set
    private var bufferedImage: BufferedImage? = null
    private val loadedTexture by lazy { DynamicTexture(bufferedImage) }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val glyphPage =
            other as GlyphPage
        return EqualsBuilder()
            .append(isAntiAliasingEnabled, glyphPage.isAntiAliasingEnabled)
            .append(isFractionalMetricsEnabled, glyphPage.isFractionalMetricsEnabled)
            .append(font, glyphPage.font)
            .isEquals
    }

    override fun hashCode(): Int {
        return HashCodeBuilder(17, 37)
            .append(font)
            .append(isAntiAliasingEnabled)
            .append(isFractionalMetricsEnabled)
            .toHashCode()
    }

    fun generateGlyphPage(chars: CharArray) {
        // Calculate glyphPageSize
        var maxWidth = -1.0
        var maxHeight = -1.0
        val affineTransform = AffineTransform()
        val fontRenderContext = FontRenderContext(affineTransform, isAntiAliasingEnabled, isFractionalMetricsEnabled)
        for (ch in chars) {
            val bounds =
                font.getStringBounds(ch.toString(), fontRenderContext)
            if (maxWidth < bounds.width) maxWidth = bounds.width
            if (maxHeight < bounds.height) maxHeight = bounds.height
        }

        // Leave some additional space
        maxWidth += 2.0
        maxHeight += 2.0
        imgSize = ceil(
            ceil(sqrt(maxWidth * maxWidth * chars.size) / maxWidth)
                .coerceAtLeast(ceil(sqrt(maxHeight * maxHeight * chars.size) / maxHeight))
                    * maxWidth.coerceAtLeast(maxHeight)
        ).toInt() + 1

        bufferedImage = BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_ARGB)
        val g = bufferedImage!!.graphics as Graphics2D

        g.font = font
        g.color = Color(255, 255, 255, 0)
        g.fillRect(0, 0, imgSize, imgSize)

        g.color = Color.white

        g.setRenderingHint(
            RenderingHints.KEY_FRACTIONALMETRICS,
            if (isFractionalMetricsEnabled) RenderingHints.VALUE_FRACTIONALMETRICS_ON else RenderingHints.VALUE_FRACTIONALMETRICS_OFF
        )
        g.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            if (isAntiAliasingEnabled) RenderingHints.VALUE_ANTIALIAS_OFF else RenderingHints.VALUE_ANTIALIAS_ON
        )
        g.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            if (isAntiAliasingEnabled) RenderingHints.VALUE_TEXT_ANTIALIAS_ON else RenderingHints.VALUE_TEXT_ANTIALIAS_OFF
        )
        val fontMetrics = g.fontMetrics
        var currentCharHeight = 0
        var posX = 0
        var posY = 1
        for (ch in chars) {
            val glyph = Glyph()
            val bounds = fontMetrics.getStringBounds(ch.toString(), g)
            glyph.width = bounds.bounds.width + 8 // Leave some additional space
            glyph.height = bounds.height.toInt()
            check(posY + glyph.height < imgSize) { "Not all characters will fit" }
            if (ch == 'j') glyph.width += 4
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
            g.drawString(ch.toString(), posX + if (ch == 'j') 5 else 2, posY + fontMetrics.ascent)
            posX += glyph.width
            glyphCharacterMap[ch] = glyph
        }
    }

    fun bindTexture() {
        GlStateManager.bindTexture(loadedTexture.glTextureId)
    }

    fun unbindTexture() {
        GlStateManager.bindTexture(0)
    }

    fun drawChar(ch: Char, x: Float, y: Float): Float {
        val glyph = glyphCharacterMap[if (ch == '▏') '|' else ch] ?: return (-1).toFloat()
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

    class Glyph {
        var x = 0
        var y = 0
        var width = 0
        var height = 0

        internal constructor(x: Int, y: Int, width: Int, height: Int) {
            this.x = x
            this.y = y
            this.width = width
            this.height = height
        }

        internal constructor()

    }

}