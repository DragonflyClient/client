package net.inceptioncloud.minecraftmod.render.font;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.apache.logging.log4j.LogManager;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * The Glyph Page Font Renderer creates Glyph Pages of different fonts to easily render them ingame.
 * <p>
 * It can be used as an {@link IFontRenderer} to dynamically switch between the Minecraft default
 * and this one.
 */
public class CustomFontRenderer implements IFontRenderer
{
    /**
     * Contains all already loaded fonts.
     */
    public static final List<String> LOADED_FONTS = new ArrayList<>();

    /**
     * Current X coordinate at which to draw the next character.
     */
    private float posX;

    /**
     * Current Y coordinate at which to draw the next character.
     */
    private float posY;

    /**
     * Array of RGB triplets defining the 16 standard chat colors followed by 16 darker version of the same colors for
     * drop shadows.
     */
    private int[] colorCode = new int[32];

    /**
     * Used to specify new red value for the current color.
     */
    private float red;
    /**
     * Used to specify new blue value for the current color.
     */
    private float blue;

    /**
     * Used to specify new green value for the current color.
     */
    private float green;

    /**
     * Used to speify new alpha value for the current color.
     */
    private float alpha;

    /**
     * Set if the "k" style (random) is active in currently rendering string
     */
    private boolean randomStyle;

    /**
     * Set if the "l" style (bold) is active in currently rendering string
     */
    private boolean boldStyle;

    /**
     * Set if the "o" style (italic) is active in currently rendering string
     */
    private boolean italicStyle;

    /**
     * Set if the "n" style (underlined) is active in currently rendering string
     */
    private boolean underlineStyle;

    /**
     * Set if the "m" style (strikethrough) is active in currently rendering string
     */
    private boolean strikethroughStyle;

    /**
     * The different Glyph Pages
     */
    private GlyphPage regularGlyphPage, boldGlyphPage, italicGlyphPage, boldItalicGlyphPage;

    /**
     * Default Constructor
     */
    public CustomFontRenderer (GlyphPage regularGlyphPage, GlyphPage boldGlyphPage, GlyphPage italicGlyphPage, GlyphPage boldItalicGlyphPage)
    {
        this.regularGlyphPage = regularGlyphPage;
        this.boldGlyphPage = boldGlyphPage;
        this.italicGlyphPage = italicGlyphPage;
        this.boldItalicGlyphPage = boldItalicGlyphPage;

        for (int i = 0 ; i < 32 ; ++i) {
            int j = ( i >> 3 & 1 ) * 85;
            int k = ( i >> 2 & 1 ) * 170 + j;
            int l = ( i >> 1 & 1 ) * 170 + j;
            int i1 = ( i & 1 ) * 170 + j;

            if (i == 6) {
                k += 85;
            }


            if (i >= 16) {
                k /= 4;
                l /= 4;
                i1 /= 4;
            }

            this.colorCode[i] = ( k & 255 ) << 16 | ( l & 255 ) << 8 | i1 & 255;
        }
    }

    /**
     * Convenient Builder
     */
    public static CustomFontRenderer create (String fontName, int size, boolean bold, boolean italic, boolean boldItalic)
    {
        LogManager.getLogger().info("Building GlyphPageFontRenderer for Font {} with size {} and style {{},{},{}}", fontName, size, bold, italic, boldItalic);

        // If the font isn't already loaded, import it from a .ttf file
        if (!LOADED_FONTS.contains(fontName)) {
            try {
                // Load the graphics environment
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("fonts/" + fontName + ".ttf")));
                LogManager.getLogger().debug("Importing font {}...", fontName);
                LOADED_FONTS.add(fontName);
            } catch (FontFormatException | IOException e) {
                e.printStackTrace();
            }
        }

        List<Character> characterList = new ArrayList<>();

        for (int i = 0 ; i < 256 ; i++) {
            characterList.add(( char ) i);
        }

        final char[] chars = new char[characterList.size()];

        for (int i = 0 ; i < characterList.size() ; i++) {
            chars[i] = characterList.get(i);
        }

        GlyphPage regularPage = new GlyphPage(new Font(fontName, Font.PLAIN, size), true, true);
        regularPage.generateGlyphPage(chars);
        regularPage.setupTexture();

        GlyphPage boldPage = regularPage;
        GlyphPage italicPage = regularPage;
        GlyphPage boldItalicPage = regularPage;

        if (bold) {
            boldPage = new GlyphPage(new Font(fontName, Font.BOLD, size), true, true);

            boldPage.generateGlyphPage(chars);
            boldPage.setupTexture();
        }

        if (italic) {
            italicPage = new GlyphPage(new Font(fontName, Font.ITALIC, size), true, true);

            italicPage.generateGlyphPage(chars);
            italicPage.setupTexture();
        }

        if (boldItalic) {
            boldItalicPage = new GlyphPage(new Font(fontName, Font.BOLD | Font.ITALIC, size), true, true);

            boldItalicPage.generateGlyphPage(chars);
            boldItalicPage.setupTexture();
        }

        return new CustomFontRenderer(regularPage, boldPage, italicPage, boldItalicPage);
    }

    /**
     * Draw a left-justified string at the given location with a specific color.
     *
     * @see #drawString(String, float, float, int, boolean) Parameter Description
     */
    @Override
    public int drawString (final String text, final int x, final int y, final int color)
    {
        return drawString(text, x, y, color, false);
    }

    /**
     * Draws the specified string.
     */
    @Override
    public int drawString (String text, float x, float y, int color, boolean dropShadow)
    {
        y -= 3;
        GlStateManager.enableAlpha();
        this.resetStyles();
        int i;

        if (dropShadow) {
            i = this.renderString(text, x + 1.0F, y + 1.0F, color, true);
            i = Math.max(i, this.renderString(text, x, y, color, false));
        } else {
            i = this.renderString(text, x, y, color, false);
        }

        return i;
    }

    /**
     * Draw a left-justified string at the given location with a specific color and
     * a shadow.
     *
     * @see #drawString(String, float, float, int, boolean) Parameter Description
     */
    @Override
    public int drawStringWithShadow (final String text, final float x, final float y, final int color)
    {
        return drawString(text, x, y, color, true);
    }

    /**
     * Get the width of a string in the current font.
     *
     * @param text The text
     *
     * @return The width in pixels
     */
    @Override
    public int getStringWidth (final String text)
    {
        if (text == null) {
            return 0;
        }

        int width = 0;
        int size = text.length();

        boolean on = false;

        for (int i = 0 ; i < size ; i++) {
            char character = text.charAt(i);

            if (character == '§')
                on = true;
            else if (on && character >= '0' && character <= 'r') {
                int colorIndex = "0123456789abcdefklmnor".indexOf(character);
                if (colorIndex < 16) {
                    boldStyle = false;
                    italicStyle = false;
                } else if (colorIndex == 17) {
                    boldStyle = true;
                } else if (colorIndex == 20) {
                    italicStyle = true;
                } else if (colorIndex == 21) {
                    boldStyle = false;
                    italicStyle = false;
                }
//                i++;
                on = false;
            } else {
                if (on) i--;

                character = text.charAt(i);
                width += getCharWidthFloat(character);
            }
        }

        return width / 2;
    }

    /**
     * @param c The character
     *
     * @return {@link #getCharWidthFloat(char)} rounded to an integer value.
     */
    @Override
    public int getCharWidth (final char c)
    {
        return ( ( int ) getCharWidthFloat(c) );
    }

    /**
     * @return The default character height
     */
    @Override
    public int getHeight ()
    {
        return ( int ) ( regularGlyphPage.getMaxFontHeight() / 2.2D );
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
        return getCurrentGlyphPage().getWidth(c) - 8;
    }

    /**
     * Render single line string by setting GL color, current (posX,posY), and calling renderStringAtPos()
     */
    private int renderString (String text, float x, float y, int color, boolean dropShadow)
    {
        if (text == null) {
            return 0;
        } else {

            if (( color & -67108864 ) == 0) {
                color |= -16777216;
            }

            if (dropShadow) {
                color = ( color & 16579836 ) >> 2 | color & -16777216;
            }

            this.red = ( float ) ( color >> 16 & 255 ) / 255.0F;
            this.blue = ( float ) ( color >> 8 & 255 ) / 255.0F;
            this.green = ( float ) ( color & 255 ) / 255.0F;
            this.alpha = ( float ) ( color >> 24 & 255 ) / 255.0F;
            GlStateManager.color(this.red, this.blue, this.green, this.alpha);
            this.posX = x * 2.0f;
            this.posY = y * 2.0f;
            this.renderStringAtPos(text, dropShadow);
            return ( int ) ( this.posX / 2.0f ); /* NOTE: This was originally 4.0F */
        }
    }

    /**
     * Render a single line string at the current (posX,posY) and update posX
     */
    private void renderStringAtPos (String text, boolean shadow)
    {
        GlyphPage glyphPage = getCurrentGlyphPage();

        glPushMatrix();
        glScaled(0.5, 0.5, 0.5);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableTexture2D();

        glyphPage.bindTexture();

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        for (int charIndex = 0 ; charIndex < text.length() ; ++charIndex) {
            char currentChar = text.charAt(charIndex);

            if (currentChar == 167 /* = § */ && charIndex + 1 < text.length()) {
                /* NOTE: This would be a temporary fix if the § has been doubled */
//                if (text.charAt(charIndex + 1) == 167)
//                    continue;

                int i1 = "0123456789abcdefklmnor".indexOf(text.toLowerCase(Locale.ENGLISH).charAt(charIndex + 1));

                if (i1 < 16) {
                    this.randomStyle = false;
                    this.boldStyle = false;
                    this.strikethroughStyle = false;
                    this.underlineStyle = false;
                    this.italicStyle = false;

                    if (i1 < 0) {
                        i1 = 15;
                    }

                    if (shadow) {
                        i1 += 16;
                    }

                    int j1 = this.colorCode[i1];
                    GlStateManager.color(( float ) ( j1 >> 16 ) / 255.0F, ( float ) ( j1 >> 8 & 255 ) / 255.0F, ( float ) ( j1 & 255 ) / 255.0F, this.alpha);
                } else if (i1 == 16) {
                    this.randomStyle = true;
                } else if (i1 == 17) {
                    this.boldStyle = true;
                } else if (i1 == 18) {
                    this.strikethroughStyle = true;
                } else if (i1 == 19) {
                    this.underlineStyle = true;
                } else if (i1 == 20) {
                    this.italicStyle = true;
                } else {
                    this.randomStyle = false;
                    this.boldStyle = false;
                    this.strikethroughStyle = false;
                    this.underlineStyle = false;
                    this.italicStyle = false;

                    GlStateManager.color(this.red, this.blue, this.green, this.alpha);
                }

                ++charIndex;
            } else {
                glyphPage = getCurrentGlyphPage();
                glyphPage.bindTexture();

                float f = glyphPage.drawChar(currentChar, posX, posY);

                if (f != -1)
                    finishDraw(f, glyphPage);
                else {
                    final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
                    final float factor = 2.1F;

                    glScaled(factor, factor, factor);

                    fontRenderer.setPosX(( posX / factor ) + 1).setPosY(( posY / factor ) + 2).renderUnicodeChar(currentChar, false);
                    this.posX += fontRenderer.getCharWidthFloat(currentChar) * factor;

                    glScaled(1 / factor, 1 / factor, 1 / factor);
                }
            }
        }

        glyphPage.unbindTexture();

        glPopMatrix();
    }

    private void finishDraw (float f, GlyphPage glyphPage)
    {
        if (this.strikethroughStyle) {
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            GlStateManager.disableTexture2D();
            worldrenderer.begin(7, DefaultVertexFormats.POSITION);
            worldrenderer.pos(this.posX, this.posY + ( float ) ( glyphPage.getMaxFontHeight() / 2 ) + 2, 0.0D).endVertex();
            worldrenderer.pos(this.posX + f, this.posY + ( float ) ( glyphPage.getMaxFontHeight() / 2 ) + 2, 0.0D).endVertex();
            worldrenderer.pos(this.posX + f, this.posY + ( float ) ( glyphPage.getMaxFontHeight() / 2 ) + 1, 0.0D).endVertex();
            worldrenderer.pos(this.posX, this.posY + ( float ) ( glyphPage.getMaxFontHeight() / 2 ) + 1, 0.0D).endVertex();
            tessellator.draw();
            GlStateManager.enableTexture2D();
        }

        if (this.underlineStyle) {
            Tessellator tessellator1 = Tessellator.getInstance();
            WorldRenderer worldrenderer1 = tessellator1.getWorldRenderer();
            GlStateManager.disableTexture2D();
            worldrenderer1.begin(7, DefaultVertexFormats.POSITION);
            int l = this.underlineStyle ? -1 : 0;
            worldrenderer1.pos(this.posX + ( float ) l, this.posY + ( float ) glyphPage.getMaxFontHeight() - 3.0F, 0.0D).endVertex();
            worldrenderer1.pos(this.posX + f, this.posY + ( float ) glyphPage.getMaxFontHeight() - 3.0F, 0.0D).endVertex();
            worldrenderer1.pos(this.posX + f, this.posY + ( float ) glyphPage.getMaxFontHeight() - 4.0F, 0.0D).endVertex();
            worldrenderer1.pos(this.posX + ( float ) l, this.posY + ( float ) glyphPage.getMaxFontHeight() - 4.0F, 0.0D).endVertex();
            tessellator1.draw();
            GlStateManager.enableTexture2D();
        }

        this.posX += f;
    }


    private GlyphPage getCurrentGlyphPage ()
    {
        if (boldStyle && italicStyle)
            return boldItalicGlyphPage;
        else if (boldStyle)
            return boldGlyphPage;
        else if (italicStyle)
            return italicGlyphPage;
        else
            return regularGlyphPage;
    }

    /**
     * Reset all style flag fields in the class to false; called at the start of string rendering
     */
    private void resetStyles ()
    {
        this.randomStyle = false;
        this.boldStyle = false;
        this.italicStyle = false;
        this.underlineStyle = false;
        this.strikethroughStyle = false;
    }

    /**
     * Trims a string to fit a specified Width.
     */
    @Override
    public String trimStringToWidth (String text, int width)
    {
        return this.trimStringToWidth(text, width, false);
    }

    /**
     * Trims a string to a specified width, and will reverse it if par3 is set.
     */
    @Override
    public String trimStringToWidth (String text, int maxWidth, boolean reverse)
    {
        StringBuilder stringbuilder = new StringBuilder();

        boolean on = false;

        int j = reverse ? text.length() - 1 : 0;
        int k = reverse ? -1 : 1;
        int width = 0;

        GlyphPage currentPage;

        for (int i = j ; i >= 0 && i < text.length() && i < maxWidth ; i += k) {
            char character = text.charAt(i);

            if (character == '§')
                on = true;
            else if (on && character >= '0' && character <= 'r') {
                int colorIndex = "0123456789abcdefklmnor".indexOf(character);
                if (colorIndex < 16) {
                    boldStyle = false;
                    italicStyle = false;
                } else if (colorIndex == 17) {
                    boldStyle = true;
                } else if (colorIndex == 20) {
                    italicStyle = true;
                } else if (colorIndex == 21) {
                    boldStyle = false;
                    italicStyle = false;
                }
                i++;
                on = false;
            } else {
                if (on) i--;

                character = text.charAt(i);
                currentPage = getCurrentGlyphPage();
                width += ( currentPage.getWidth(character) - 8 ) / 1;
            }

            if (i > width) {
                break;
            }

            if (reverse) {
                stringbuilder.insert(0, character);
            } else {
                stringbuilder.append(character);
            }
        }

        return stringbuilder.toString();
    }
}