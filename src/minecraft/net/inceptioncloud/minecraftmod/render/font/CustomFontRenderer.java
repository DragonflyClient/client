package net.inceptioncloud.minecraftmod.render.font;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class CustomFontRenderer implements IFontRenderer
{
    /**
     * Contains all already loaded fonts.
     */
    private static final List<String> loadedFonts = new ArrayList<>();

    private final UnicodeFont unicodeFont;
    private final int[] colorCodes = new int[32];

    @Getter
    private int fontType, size;

    @Getter
    private String fontName;

    private float kerning;

    /**
     * CustomFontRenderer Constructor
     *
     * @param fontName Font name (name of .ttf file in fonts/..)
     * @param fontType Font type (Plain, Bold, ..)
     * @param size Font size
     */
    public CustomFontRenderer (String fontName, int fontType, int size)
    {
        this(fontName, fontType, size, 0);
    }

    /**
     * Full Constructor
     *
     * @param fontName Font name (name of .ttf file in fonts/..)
     * @param fontType Font type (Plain, Bold, ..)
     * @param size Font size
     * @param kerning The space between the letters
     */
    public CustomFontRenderer (String fontName, int fontType, int size, float kerning)
    {
        // Load the graphics environment
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        // If the font isn't already loaded, import it from a .ttf file
        if (!loadedFonts.contains(fontName)) {
            try {
                ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("fonts/" + fontName + ".ttf")));
                LogManager.getLogger().debug("Importing font {}...", fontName);
                loadedFonts.add(fontName);
            } catch (FontFormatException | IOException e) {
                e.printStackTrace();
            }
        }

        this.fontName = fontName;
        this.fontType = fontType;
        this.size = size;

        this.unicodeFont = new UnicodeFont(new Font(fontName, fontType, size));
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

    /**
     * Draw a left-justified string at the given location with a specific color.
     *
     * @see #drawString(String, float, float, int, boolean) Parameter Description
     */
    @Override
    public int drawString (final String text, final int x, final int y, final int color)
    {
        return drawString(text, ( float ) x, ( float ) y, color);
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
    public int drawString (final String text, final float x, final float y, final int color, final boolean dropShadow)
    {
        return drawStringWithShadow(text, x, y, color);
    }

    /**
     * Draw a left-justified string at the given location with a specific color.
     * An optional shadow can be drawn.
     *
     * @param text       The text to draw
     * @param x          The x location
     * @param y          The y location
     * @param color      The color
     *
     * @return The end x-value
     */
    public int drawString (String text, float x, float y, int color)
    {
        x *= 2.0F;
        y *= 2.0F;
        float originalX = x;

        GL11.glPushMatrix();
        GL11.glScaled(0.5F, 0.5F, 0.5F);

        boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean lighting = GL11.glIsEnabled(GL11.GL_LIGHTING);
        boolean texture = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
        if (!blend)
            glEnable(GL11.GL_BLEND);
        if (lighting)
            glDisable(GL11.GL_LIGHTING);
        if (texture)
            glDisable(GL11.GL_TEXTURE_2D);

        int currentColor = color;
        char[] characters = text.toCharArray();

        int index = 0;
        for (char c : characters) {
            if (c == '\r') {
                x = originalX;
            }
            if (c == '\n') {
                y += getSpecificHeight(Character.toString(c)) * 2.0F;
            }
            if (c != '\247' && ( index == 0 || index == characters.length - 1 || characters[index - 1] != '\247' )) {
                unicodeFont.drawString(x, y, Character.toString(c), new org.newdawn.slick.Color(currentColor));
                x += ( getStringWidth(Character.toString(c)) * 2.0F );
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
        glPopMatrix();
        return ( int ) x;
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
        drawString(StringUtils.stripControlCodes(text), x + 0.5F, y + 0.5F, 0x000000);
        return drawString(text, x, y, color);
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
        float width = 0.0F;

        String str = StringUtils.stripControlCodes(text);
        for (char c : str.toCharArray()) {
            width += unicodeFont.getWidth(Character.toString(c)) + this.kerning;
        }

        return ( int ) ( width / 2.0F );
    }

    /**
     * @param c The character
     *
     * @return {@link #getCharWidthFloat(char)} rounded to an integer value.
     */
    @Override
    public int getCharWidth (char c)
    {
        return unicodeFont.getWidth(String.valueOf(c));
    }

    /**
     * @return The default character height
     */
    @Override
    public int getHeight ()
    {
        return unicodeFont.getLineHeight();
    }

    /**
     * The exact with of the specific char in the current font.
     *
     * @param c The character
     *
     * @return The width in pixels
     */
    @Override
    public float getCharWidthFloat (final char c)
    {
        return getCharWidth(c);
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
            float var12 = this.getCharWidth(var11);

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
     * @return {@link #unicodeFont}
     */
    public UnicodeFont getFont ()
    {
        return this.unicodeFont;
    }

    /**
     * @return The specific height of a char sequence
     */
    public float getSpecificHeight (String s)
    {
        return unicodeFont.getHeight(s) / 2.0F;
    }

    /**
     * Draw a center-justified string.
     */
    public void drawCenteredString (String text, float x, float y, int color)
    {
        drawString(text, x - ( int ) ( getStringWidth(text) / 2D ), y, color);
    }

    /**
     * Draw a center-justified string with shadow.
     */
    public void drawCenteredStringWithShadow (String text, float x, float y, int color)
    {
        drawCenteredString(StringUtils.stripControlCodes(text), x + 0.5F, y + 0.5F, color);
        drawCenteredString(text, x, y, color);
    }
}