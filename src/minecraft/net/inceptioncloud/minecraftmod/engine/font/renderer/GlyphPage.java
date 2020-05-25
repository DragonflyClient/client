package net.inceptioncloud.minecraftmod.engine.font.renderer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;

public class GlyphPage
{
    private int imgSize;
    private double maxFontHeight = -1;
    private final Font font;
    private final boolean antiAliasing;
    private final boolean fractionalMetrics;
    public HashMap<Character, Glyph> glyphCharacterMap = new HashMap<>();

    private BufferedImage bufferedImage;
    private DynamicTexture loadedTexture;

    public GlyphPage(Font font, boolean antiAliasing, boolean fractionalMetrics) {
        this.font = font;
        this.antiAliasing = antiAliasing;
        this.fractionalMetrics = fractionalMetrics;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        GlyphPage glyphPage = (GlyphPage) o;

        return new EqualsBuilder()
                .append(antiAliasing, glyphPage.antiAliasing)
                .append(fractionalMetrics, glyphPage.fractionalMetrics)
                .append(font, glyphPage.font)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(font)
                .append(antiAliasing)
                .append(fractionalMetrics)
                .toHashCode();
    }

    public void generateGlyphPage(char[] chars) {
        // Calculate glyphPageSize
        double maxWidth = -1;
        double maxHeight = -1;

        AffineTransform affineTransform = new AffineTransform();
        FontRenderContext fontRenderContext = new FontRenderContext(affineTransform, antiAliasing, fractionalMetrics);

        for (char ch : chars) {
            Rectangle2D bounds = font.getStringBounds(Character.toString(ch), fontRenderContext);

            if (maxWidth < bounds.getWidth()) maxWidth = bounds.getWidth();
            if (maxHeight < bounds.getHeight()) maxHeight = bounds.getHeight();
        }

        // Leave some additional space
        maxHeight += 2;

        imgSize = ( int ) Math.ceil(
            Math.max(Math.ceil(Math.sqrt(maxWidth * maxWidth * chars.length) / maxWidth),
            Math.ceil(Math.sqrt(maxHeight * maxHeight * chars.length) / maxHeight))
                                    * Math.max(maxWidth, maxHeight)) + 1;

        bufferedImage = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = ( Graphics2D ) bufferedImage.getGraphics();

        g.setFont(font);
        // Set Color to Transparent
        g.setColor(new Color(255, 255, 255, 0));
        // Set the image background to transparent
        g.fillRect(0, 0, imgSize, imgSize);

        g.setColor(Color.white);

        g.setRenderingHint(
                RenderingHints.KEY_FRACTIONALMETRICS,
                fractionalMetrics ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF
        );
        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                antiAliasing ? RenderingHints.VALUE_ANTIALIAS_OFF : RenderingHints.VALUE_ANTIALIAS_ON
        );
        g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                antiAliasing ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF
        );

        FontMetrics fontMetrics = g.getFontMetrics();

        double currentCharHeight = 0;
        double posX = 0;
        double posY = 1;

        for (char ch : chars) {
            Glyph glyph = new Glyph();

            Rectangle2D bounds = fontMetrics.getStringBounds(Character.toString(ch), g);

            glyph.width = bounds.getWidth() + 6;
            glyph.height = bounds.getHeight();

            if (posY + glyph.height >= imgSize) {
                throw new IllegalStateException("Not all characters will fit");
            }

            if (ch == 'i')
                glyph.width += 8;

            if (posX + glyph.width >= imgSize) {
                posX = 0;
                posY += currentCharHeight;
                currentCharHeight = 0;
            }

            glyph.x = posX;
            glyph.y = posY;

            if (glyph.height > maxFontHeight) {
                maxFontHeight = glyph.height;
            }

            if (glyph.height > currentCharHeight)
                currentCharHeight = glyph.height;

            g.drawString(
                    Character.toString(ch),
                    (int) posX + (ch == 'j' ? 6 : 2),
                    (int) posY + fontMetrics.getAscent()
            );

            posX += glyph.width;

            glyphCharacterMap.put(ch, glyph);
        }
    }

    public void setupTexture ()
    {
        loadedTexture = new DynamicTexture(bufferedImage);
    }

    public void bindTexture ()
    {
        GlStateManager.bindTexture(loadedTexture.getGlTextureId());
    }

    public void unbindTexture ()
    {
        GlStateManager.bindTexture(0);
    }

    public float drawChar (char ch, float x, float y) {
        Glyph glyph = glyphCharacterMap.get(ch == '▏' ? '|' : ch);

        if (glyph == null) {
            return -1;
        }

        float pageX = (float) (glyph.x / (float) imgSize);
        float pageY = (float) (glyph.y / (float) imgSize);

        float pageWidth = (float) (glyph.width / (float) imgSize);
        float pageHeight = (float) (glyph.height / (float) imgSize);

        float width = (float) glyph.width;
        float height = (float) glyph.height;

//        Gui.drawRect(x, y, x + width, y + height, new Color(0, 100, 200, 40).getRGB());

        glBegin(GL_TRIANGLES);

        glTexCoord2f(pageX + pageWidth, pageY);
        glVertex2f(x + width, y);

        glTexCoord2f(pageX, pageY);
        glVertex2f(x, y);

        glTexCoord2f(pageX, pageY + pageHeight);
        glVertex2f(x, y + height);

        glTexCoord2f(pageX, pageY + pageHeight);
        glVertex2f(x, y + height);

        glTexCoord2f(pageX + pageWidth, pageY + pageHeight);
        glVertex2f(x + width, y + height);

        glTexCoord2f(pageX + pageWidth, pageY);
        glVertex2f(x + width, y);

        glEnd();

        return ch == 'i' ? width - 14 : width - 6;
    }

    public float getWidth (char ch)
    {
        return glyphCharacterMap.containsKey(ch) ? (float) glyphCharacterMap.get(ch).width : 0;
    }

    public double getMaxFontHeight() {
        return maxFontHeight;
    }

    public boolean isAntiAliasingEnabled ()
    {
        return antiAliasing;
    }

    public boolean isFractionalMetricsEnabled ()
    {
        return fractionalMetrics;
    }

    public static class Glyph {
        private double x;
        private double y;
        private double width;
        private double height;

        Glyph(double x, double y, double width, double height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        Glyph()
        {
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getWidth() {
            return width;
        }

        public double getHeight() {
            return height;
        }
    }
}