package net.inceptioncloud.dragonfly.engine.font;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A custom Font Renderer based on the Unicode.
 */
public class UnicodeFontRenderer implements IFontRenderer
{
    public final int FONT_HEIGHT = 9;
    private final int[] colorCodes = new int[32];
    private final float kerning;
    private final Map<String, Float> cachedStringWidth = new HashMap<>();
    private final float antiAliasingFactor;
    private final UnicodeFont unicodeFont;

    /**
     * Default Constructor
     *
     * @param font               The font
     * @param kerning            The kerning value
     * @param antiAliasingFactor Anti-Aliasing Factor
     */
    private UnicodeFontRenderer (Font font, float kerning, float antiAliasingFactor)
    {
        this.antiAliasingFactor = antiAliasingFactor;
        this.unicodeFont = new UnicodeFont(font);
        this.kerning = kerning;

        this.unicodeFont.addAsciiGlyphs();
        //noinspection unchecked
        this.unicodeFont.getEffects().add(new ColorEffect(java.awt.Color.WHITE));

        try {
            this.unicodeFont.loadGlyphs();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0 ; i < 32 ; i++) {
            int shadow = ( i >> 3 & 1 ) * 85;
            int red = ( i >> 2 & 1 ) * 170 + shadow;
            int green = ( i >> 1 & 1 ) * 170 + shadow;
            int blue = ( i & 1 ) * 170 + shadow;

            if (i == 6) {
                red += 85;
            }

            if (i >= 16) {
                red /= 4;
                green /= 4;
                blue /= 4;
            }

            this.colorCodes[i] = ( red & 255 ) << 16 | ( green & 255 ) << 8 | blue & 255;
        }
    }

    //<editor-fold desc="<--- Static Content --->">

    public static UnicodeFontRenderer newInstance (String name, int size, int style)
    {
        try {
            // If the font isn't already loaded, import it from a .ttf file
            if (!GlyphFontRenderer.LOADED_FONTS.contains(name)) {
                // Load the graphics environment
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("dragonfly/assets/fonts/" + name + ".ttf")));
                LogManager.getLogger().debug("Importing font {}...", name);
                GlyphFontRenderer.LOADED_FONTS.add(name);
            }
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }

        final Font font = new Font(name, style, size);
        return new UnicodeFontRenderer(font, 0, 3);
    }
    //</editor-fold>

    /**
     * Draw a left-justified string at the given location with a specific color.
     *
     * @see #drawString(String, float, float, int, boolean) Parameter Description
     */
    @Override
    public int drawString (String text, int x, int y, int color)
    {
        return drawString(text, ( float ) x, ( float ) y, color, false);
    }

    /**
     * Draw a left-justified string at the given location with a specific color.
     * An optional shadow can be drawn.
     *
     * @param text       The text to draw
     * @param x          The x location
     * @param y          The y location
     * @param color      The color
     * @param dropShadow Whether to draw a shadow
     *
     * @return The end x-value
     */
    @Override
    public int drawString (String text, float x, float y, int color, boolean dropShadow)
    {
        if (dropShadow)
            return drawStringWithShadow(text, x, y, color);

        if (text == null)
            return 0;

        x *= 2.0F;
        y *= 2.0F;

        float originalX = x;

        GL11.glPushMatrix();
        GlStateManager.scale(1 / antiAliasingFactor, 1 / antiAliasingFactor, 1 / antiAliasingFactor);
        GL11.glScaled(0.5F, 0.5F, 0.5F);
        x *= antiAliasingFactor;
        y *= antiAliasingFactor;
        float red = ( float ) ( color >> 16 & 255 ) / 255.0F;
        float green = ( float ) ( color >> 8 & 255 ) / 255.0F;
        float blue = ( float ) ( color & 255 ) / 255.0F;
        float alpha = ( float ) ( color >> 24 & 255 ) / 255.0F;
        GlStateManager.color(red, green, blue, alpha);

        boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean lighting = GL11.glIsEnabled(GL11.GL_LIGHTING);
        boolean texture = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
        if (!blend)
            GL11.glEnable(GL11.GL_BLEND);
        if (lighting)
            GL11.glDisable(GL11.GL_LIGHTING);
        if (texture)
            GL11.glDisable(GL11.GL_TEXTURE_2D);

        int currentColor = color;
        char[] characters = text.toCharArray();

        int index = 0;
        for (char c : characters) {
            if (c == '\r') {
                x = originalX;
            }
            if (c == '\n') {
                y += getHeight() * 2.0F;
            }
            if (c != '\247' && ( index == 0 || index == characters.length - 1 || characters[index - 1] != '\247' )) {
                //Line causing error
                unicodeFont.drawString(x, y, Character.toString(c), new org.newdawn.slick.Color(currentColor));
                x += ( getUnicodeWidth(Character.toString(c)) * 2.0F * antiAliasingFactor );
            } else if (c == ' ') {
                x += unicodeFont.getSpaceWidth();
            } else if (c == '\247' && index != characters.length - 1) {
                int codeIndex = "0123456789abcdefg".indexOf(text.charAt(index + 1));
                if (codeIndex < 0) continue;

                currentColor = this.colorCodes[codeIndex];
            }

            index++;
        }

        GL11.glScaled(2.0F, 2.0F, 2.0F);
        if (texture)
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        if (lighting)
            GL11.glEnable(GL11.GL_LIGHTING);
        if (!blend)
            GL11.glDisable(GL11.GL_BLEND);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
        return ( int ) x / 2;
    }

    @Override
    public int drawStringWithCustomShadow(String text, int x, int y, int color, int shadowColor, float distance) {
        drawString(StringUtils.stripControlCodes(text), x + distance, y + distance, shadowColor, false);
        return drawString(text, x, y, color, false);
    }

    /**
     * Draw a left-justified string at the given location with a specific color and
     * a shadow.
     *
     * @see #drawString(String, float, float, int, boolean) Parameter Description
     */
    @Override
    public int drawStringWithShadow (String text, float x, float y, int color)
    {
        drawString(StringUtils.stripControlCodes(text), x + 0.5F, y + 0.5F, 0x000000, false);
        return drawString(text, x, y, color, false);
    }

    /**
     * Get the width of a string in the current font.
     *
     * @param text The text
     *
     * @return The width in pixels
     */
    @Override
    public int getStringWidth (String text)
    {
        if (text == null)
            return 0;

        int i = 0;
        boolean flag = false;

        for (int j = 0 ; j < text.length() ; ++j) {
            char c0 = text.charAt(j);
            float k = this.getUnicodeWidth(String.valueOf(c0));

            if (k < 0 && j < text.length() - 1) {
                ++j;
                c0 = text.charAt(j);

                if (c0 != 'l' && c0 != 'L') {
                    if (c0 == 'r' || c0 == 'R') {
                        flag = false;
                    }
                } else {
                    flag = true;
                }

                k = 0;
            }

            i += k;

            if (flag && k > 0) {
                ++i;
            }
        }

        return i;
    }

    /**
     * The exact with of the specific char in the current font.
     *
     * @param c The character
     *
     * @return The width in pixels
     */
    @Override
    public float getCharWidthFloat (char c)
    {
        return unicodeFont.getWidth(String.valueOf(c));
    }

    /**
     * @param c The character
     *
     * @return {@link #getCharWidthFloat(char)} rounded to an integer value.
     */
    @Override
    public int getCharWidth (final char c)
    {
        return ( int ) getCharWidthFloat(c);
    }

    /**
     * @return The default character height
     */
    @Override
    public int getHeight ()
    {
        return unicodeFont.getHeight("A") / 2;
    }

    /**
     * Trims the given string to be equal or less wide than the given width.
     *
     * @param text  The text
     * @param width The target with
     *
     * @return The trimmed string
     */
    @Override
    public String trimStringToWidth (String text, int width)
    {
        StringBuilder var4 = new StringBuilder();
        float var5 = 0.0F;
        int var6 = 0;
        int var7 = 1;
        boolean var8 = false;
        boolean var9 = false;

        for (int var10 = var6 ; var10 >= 0 && var10 < text.length() && var5 < ( float ) width ; var10 += var7) {
            char var11 = text.charAt(var10);
            float var12 = this.getCharWidthFloat(var11);

            if (var8) {
                var8 = false;

                if (var11 != 108 && var11 != 76) {
                    if (var11 == 114 || var11 == 82) {
                        var9 = false;
                    }
                } else {
                    var9 = true;
                }
            } else if (var12 < 0.0F) {
                var8 = true;
            } else {
                var5 += var12;

                if (var9) {
                    ++var5;
                }
            }

            if (var5 > ( float ) width) {
                break;
            } else {
                var4.append(var11);
            }
        }

        return var4.toString();
    }

    /**
     * Trims the given string to be equal or less wide than the given width.
     *
     * @param text    The text
     * @param width   The target with
     * @param reverse Whether to reverse the string
     *
     * @return The trimmed string
     */
    @Override
    public String trimStringToWidth (final String text, final int width, final boolean reverse)
    {
        throw new UnsupportedOperationException("Not supported in the Unicode Font Renderer!");
    }

    /**
     * Breaks a string into a list of pieces that will fit a specified width.
     *
     * @param text  The text
     * @param width The target width
     *
     * @return The list of broken strings
     */
    @Override
    public List<String> listFormattedStringToWidth (final String text, final int width)
    {
        throw new UnsupportedOperationException("Not supported!");
    }

    /**
     * Inserts newline and formatting into a string to wrap it within the specified width.
     *
     * @param text  The text
     * @param width The target width
     *
     * @return The string with new lines determined via \n
     */
    @Override
    public String wrapFormattedStringToWidth (final String text, final int width)
    {
        throw new UnsupportedOperationException("Not supported!");
    }

    /**
     * Determines how many characters from the string will fit into the specified width.
     *
     * @param text  The text
     * @param width The target width
     *
     * @return The amount of characters
     */
    @Override
    public int sizeStringToWidth (final String text, final int width)
    {
        throw new UnsupportedOperationException("Not supported!");
    }

    //<editor-fold desc="<--- Extra Content --->">
    public UnicodeFont getFont ()
    {
        return this.unicodeFont;
    }

    public void drawCenteredString (String text, float x, float y, int color)
    {
        drawString(text, ( float ) ( x - getUnicodeWidth(text) / 2D ), y, color, false);
    }

    public void drawStringScaled (String text, int givenX, int givenY, int color, double givenScale)
    {
        GL11.glPushMatrix();
        GL11.glTranslated(givenX, givenY, 0);
        GL11.glScaled(givenScale, givenScale, givenScale);
        drawString(text, 0, 0, color);
        GL11.glPopMatrix();
    }

    public void drawCenteredStringScaled (String text, int givenX, int givenY, int color, double givenScale)
    {
        GL11.glPushMatrix();
        GL11.glTranslated(givenX, givenY, 0);
        GL11.glScaled(givenScale, givenScale, givenScale);
        drawCenteredString(text, 0, 0, color);
        GL11.glPopMatrix();
    }

    public void drawCenteredStringWithShadow (String text, float x, float y, int color)
    {
        drawCenteredString(StringUtils.stripControlCodes(text), x + 0.5F, y + 0.5F, color);
        drawCenteredString(text, x, y, color);
    }

    public float getUnicodeWidth (String s)
    {
        if (cachedStringWidth.size() > 1000)
            cachedStringWidth.clear();
        return cachedStringWidth.computeIfAbsent(s, e ->
        {
            float width = 0.0F;
            String str = StringUtils.stripControlCodes(s);
            for (char c : str.toCharArray()) {
                width += unicodeFont.getWidth(Character.toString(c)) + this.kerning;
            }

            return width / 2.0F / antiAliasingFactor;
        });

    }

    public void drawSplitString (ArrayList<String> lines, int x, int y, int color)
    {
        drawString(
            String.join("\n\r", lines),
            x,
            y,
            color
        );
    }

    public List<String> splitString (String text, int wrapWidth)
    {
        List<String> lines = new ArrayList<>();

        String[] splitText = text.split(" ");
        StringBuilder currentString = new StringBuilder();

        for (String word : splitText) {
            String potential = currentString + " " + word;

            if (getUnicodeWidth(potential) >= wrapWidth) {
                lines.add(currentString.toString());
                currentString = new StringBuilder();
            }
            currentString.append(word).append(" ");
        }
        lines.add(currentString.toString());
        return lines;
    }
    //</editor-fold>

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        UnicodeFontRenderer that = (UnicodeFontRenderer) o;

        return new EqualsBuilder()
                .append(unicodeFont.getFont(), that.unicodeFont.getFont())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(unicodeFont.getFont())
                .toHashCode();
    }
}