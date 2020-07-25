package net.inceptioncloud.dragonfly.engine.font.renderer;

import net.inceptioncloud.dragonfly.engine.GraphicsEngine;
import net.inceptioncloud.dragonfly.engine.font.FontManager;
import net.inceptioncloud.dragonfly.engine.font.GlyphPage;
import net.inceptioncloud.dragonfly.options.sections.OptionsSectionUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
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
public class GlyphFontRenderer implements IFontRenderer {
    /**
     * Contains all already loaded fonts.
     */
    public static final List<String> LOADED_FONTS = new ArrayList<>();

    /**
     * Array of RGB triplets defining the 16 standard chat colors followed by 16 darker version of the same colors for
     * drop shadows.
     */
    private final int[] colorCode = new int[32];

    /**
     * The different Glyph Pages
     */
    private final GlyphPage pageRegular, pageBold, pageItalic, pageBoldItalic;

    private final GlyphPage unscaledPageRegular, unscaledPageBold, unscaledPageItalic, unscaledPageBoldItalic;

    /**
     * Random used for generating chars when the {@link #randomStyle} is enabled.
     */
    private final Random fontRandom = new Random();

    /**
     * Current X coordinate at which to draw the next character.
     */
    private float posX;

    /**
     * Current Y coordinate at which to draw the next character.
     */
    private float posY;

    /**
     * Used to specify new red value for the current color.
     */
    private float red;

    /**
     * Used to specify new blue value for the current color.
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
     * Used to specify new green value for the current color.
     */
    private float blue;

    /**
     * Default Constructor
     */
    public GlyphFontRenderer(
            GlyphPage pageRegular,
            GlyphPage pageBold,
            GlyphPage pageItalic,
            GlyphPage pageBoldItalic,
            GlyphPage unscaledPageRegular,
            GlyphPage unscaledPageBold,
            GlyphPage unscaledPageItalic,
            GlyphPage unscaledPageBoldItalic
    ) {
        this.pageRegular = pageRegular;
        this.pageBold = pageBold;
        this.pageItalic = pageItalic;
        this.pageBoldItalic = pageBoldItalic;

        this.unscaledPageRegular = unscaledPageRegular;
        this.unscaledPageBold = unscaledPageBold;
        this.unscaledPageItalic = unscaledPageItalic;
        this.unscaledPageBoldItalic = unscaledPageBoldItalic;

        for (int i = 0; i < 32; ++i) {
            int j = (i >> 3 & 1) * 85;
            int k = (i >> 2 & 1) * 170 + j;
            int l = (i >> 1 & 1) * 170 + j;
            int i1 = (i & 1) * 170 + j;

            if (i == 6) {
                k += 85;
            }


            if (i >= 16) {
                k /= 4;
                l /= 4;
                i1 /= 4;
            }

            this.colorCode[i] = (k & 255) << 16 | (l & 255) << 8 | i1 & 255;
        }
    }

    /**
     * Convenient Builder
     */
    public static GlyphFontRenderer create(
            String fontName,
            int size,
            double letterSpacing
    ) {
        // If the font isn't already loaded, import it from a .ttf file
        if (!LOADED_FONTS.contains(fontName)) {
            try {
                // Load the graphics environment
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("dragonfly/assets/fonts/" + fontName + ".ttf")));
                LogManager.getLogger().debug("Importing font {}...", fontName);
                LOADED_FONTS.add(fontName);
            } catch (FontFormatException | IOException e) {
                e.printStackTrace();
            }
        }

        final char[] chars = GraphicsEngine.CHARACTERS;
        final int scaledSize = (int) (size * getFontQualityScale());

        GlyphPage regularPage = new GlyphPage(makeFont(fontName, Font.PLAIN, scaledSize, letterSpacing));
        regularPage.generateGlyphPage(chars);

        GlyphPage unscaledRegular = new GlyphPage(makeFont(fontName, Font.PLAIN, size, letterSpacing));
        unscaledRegular.generateGlyphPage(chars);

        GlyphPage boldPage = new GlyphPage(makeFont(fontName, Font.BOLD, scaledSize, letterSpacing));
        boldPage.generateGlyphPage(chars);

        GlyphPage unscaledBold = new GlyphPage(makeFont(fontName, Font.BOLD, size, letterSpacing));
        unscaledBold.generateGlyphPage(chars);

        GlyphPage italicPage = new GlyphPage(makeFont(fontName, Font.ITALIC, scaledSize, letterSpacing));
        italicPage.generateGlyphPage(chars);

        GlyphPage unscaledItalic = new GlyphPage(makeFont(fontName, Font.ITALIC, size, letterSpacing));
        unscaledItalic.generateGlyphPage(chars);

        GlyphPage boldItalicPage = new GlyphPage(makeFont(fontName, Font.BOLD | Font.ITALIC, scaledSize, letterSpacing));
        boldItalicPage.generateGlyphPage(chars);

        GlyphPage unscaledBoldItalic = new GlyphPage(makeFont(fontName, Font.BOLD | Font.ITALIC, size, letterSpacing));
        unscaledBoldItalic.generateGlyphPage(chars);

        return new GlyphFontRenderer(
                regularPage, boldPage, italicPage, boldItalicPage,
                unscaledRegular, unscaledBold, unscaledItalic, unscaledBoldItalic
        );
    }

    /**
     * Quick Method to access {@link OptionsSectionUI#getFontQuality()}
     */
    public static double getFontQualityScale() {
        final double quality = OptionsSectionUI.getFontQuality().getKey().get();
        return Math.round(quality * 10.0) / 10.0;
    }

    private static Font makeFont(String name, int style, int size, double letterSpacing) {
        return FontManager.applyLetterSpacing(new Font(name, style, size), letterSpacing);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        GlyphFontRenderer that = (GlyphFontRenderer) o;

        return new EqualsBuilder()
                .append(pageRegular, that.pageRegular)
                .append(pageBold, that.pageBold)
                .append(pageItalic, that.pageItalic)
                .append(pageBoldItalic, that.pageBoldItalic)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(pageRegular)
                .append(pageBold)
                .append(pageItalic)
                .append(pageBoldItalic)
                .toHashCode();
    }

    /**
     * Draw a left-justified string at the given location with a specific color.
     *
     * @see #drawString(String, float, float, int, boolean) Parameter Description
     */
    @Override
    public int drawString(final String text, final int x, final int y, final int color) {
        return drawString(text, x, y, color, false);
    }

    /**
     * Draws the specified string.
     */
    @Override
    public int drawString(String text, float x, float y, int color, boolean dropShadow) {
        y -= 3;
        GlStateManager.enableAlpha();
        this.resetStyles();
        int i;

        if (dropShadow) {
            i = this.renderString(text, x + 0.7F, y + 0.7F, color, true);
            i = Math.max(i, this.renderString(text, x, y, color, false));
        } else {
            i = this.renderString(text, x, y, color, false);
        }

        return i;
    }

    @Override
    public int drawStringWithCustomShadow(String text, int x, int y, int color, int shadowColor, float distance) {
        y -= 3;
        GlStateManager.enableAlpha();
        this.resetStyles();
        int i;

        i = this.renderString(text, x + distance, y + distance, shadowColor, true);
        i = Math.max(i, this.renderString(text, x, y, color, false));

        return i;
    }

    /**
     * Draw a left-justified string at the given location with a specific color and
     * a shadow.
     *
     * @see #drawString(String, float, float, int, boolean) Parameter Description
     */
    @Override
    public int drawStringWithShadow(final String text, final float x, final float y, final int color) {
        return drawString(text, x, y, color, true);
    }

    /**
     * Get the width of a string in the current font.
     *
     * @param text The text
     * @return The width in pixels
     */
    @Override
    public int getStringWidth(final String text) {
        if (text == null) {
            return 0;
        }

        float width = 0;
        int size = text.length();

        boolean flag = false;

        for (int index = 0; index < size; index++) {
            char charAt = text.charAt(index);

            if (charAt == '§') {
                flag = true;
            } else if (flag && charAt >= '0' && charAt <= 'r') {
                updateStyle(charAt);
//                index++;
                flag = false;
            } else {
//                if (flag) index--;
                charAt = text.charAt(index);
                width += getCharWidthFloat(charAt);
            }
        }

        return (int) width;
    }

    private void updateStyle(char charAt) {
        int colorIndex = "0123456789abcdefklmnor".indexOf(charAt);
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
    }

    /**
     * @param c The character
     * @return {@link #getCharWidthFloat(char)} rounded to an integer value.
     */
    @Override
    public int getCharWidth(final char c) {
        return ((int) getCharWidthFloat(c));
    }

    /**
     * @return The default character height
     */
    @Override
    public int getHeight() {
        return (int) (unscaledPageRegular.getMaxFontHeight() / 2.2D);
    }

    /**
     * The exact with of the specific char in the current font.
     *
     * @param ch The character
     * @return The width in pixels
     */
    @Override
    public float getCharWidthFloat(final char ch) {
        GlyphPage.Glyph glyph = unscaledPageRegular.glyphCharacterMap.get(ch == '▏' ? '|' : ch);

        if (glyph == null) {
            return Minecraft.getMinecraft().fontRendererObj.getCharWidthFloat(ch) * 2;
        }

        return (float) ((pageRegular.getWidth(ch) - 8) / (2.0F * getFontQualityScale()));
    }

    /**
     * Render single line string by setting GL color, current (posX,posY), and calling renderStringAtPos()
     */
    private int renderString(String text, float x, float y, int color, boolean dropShadow) {
        if (text == null) {
            return 0;
        } else {

            GlStateManager.scale(1 / getFontQualityScale(), 1 / getFontQualityScale(), 1 / getFontQualityScale());
            x *= getFontQualityScale();
            y *= getFontQualityScale();

            if ((color & -67108864) == 0) {
                color |= -16777216;
            }

            if (dropShadow) {
                color = (color & 16579836) >> 2 | color & -16777216;
            }

            this.red = (float) (color >> 16 & 255) / 255.0F;
            this.green = (float) (color >> 8 & 255) / 255.0F;
            this.blue = (float) (color & 255) / 255.0F;
            this.alpha = (float) (color >> 24 & 255) / 255.0F;
            GlStateManager.color(this.red, this.green, this.blue, this.alpha);
            this.posX = x * 2.0f;
            this.posY = y * 2.0f;
            this.renderStringAtPos(text, dropShadow);
            GlStateManager.scale(getFontQualityScale(), getFontQualityScale(), getFontQualityScale());
            return (int) (this.posX / (2.0f * getFontQualityScale())); /* NOTE: This was originally 4.0F */
        }
    }

    /**
     * Render a single line string at the current (posX,posY) and update posX
     */
    private void renderStringAtPos(String text, boolean shadow) {
        GlyphPage glyphPage = getCurrentGlyphPage();

        glPushMatrix();
        glScaled(0.5, 0.5, 0.5);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableTexture2D();

        glyphPage.bindTexture();

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        float currentRed = 0, currentGreen = 0, currentBlue = 0;

        for (int charIndex = 0; charIndex < text.length(); ++charIndex) {
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
                    final float red = (float) (j1 >> 16) / 255.0F;
                    final float green = (float) (j1 >> 8 & 255) / 255.0F;
                    final float blue = (float) (j1 & 255) / 255.0F;
                    currentRed = red;
                    currentGreen = green;
                    currentBlue = blue;
                    GlStateManager.color(red, green, blue, this.alpha);
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

                    currentRed = this.red;
                    currentGreen = this.green;
                    currentBlue = this.blue;
                    GlStateManager.color(this.red, this.green, this.blue, this.alpha);
                }

                ++charIndex;
            } else {
                glyphPage = getCurrentGlyphPage();
                glyphPage.bindTexture();

                int j =
                        "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000"
                                .indexOf(currentChar);
                if (this.randomStyle && j != -1) {
                    int k = this.getCharWidth(currentChar);
                    char c1;

                    do {
                        j = this.fontRandom.nextInt(
                                "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000"
                                        .length());
                        c1 =
                                "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000"
                                        .charAt(j);
                    } while (k != this.getCharWidth(c1));

                    currentChar = c1;
                }

                float f = glyphPage.drawChar(currentChar, posX, posY);

                if (f != -1) {
                    finishDraw(f, glyphPage, currentRed, currentGreen, currentBlue);
                } else {
                    final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
                    final float factor = (float) (2F * getFontQualityScale());

                    glScaled(factor, factor, factor);

                    fontRenderer.setPosX((posX / factor) + 1).setPosY((posY / factor) + 2).renderUnicodeChar(currentChar, false);
                    this.posX += fontRenderer.getCharWidthFloat(currentChar) * factor;

                    glScaled(1 / factor, 1 / factor, 1 / factor);
                }
            }
        }

        glyphPage.unbindTexture();

        glPopMatrix();
    }

    private void finishDraw(float f, GlyphPage glyphPage, float r, float g, float b) {
        if (this.strikethroughStyle) {
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldRenderer = tessellator.getWorldRenderer();
            GlStateManager.disableTexture2D();
            GlStateManager.color(r, g, b, alpha);
            worldRenderer.begin(7, DefaultVertexFormats.POSITION);
            worldRenderer.pos(this.posX, this.posY + (float) (glyphPage.getMaxFontHeight() / 2) + 3, 0.0D).endVertex();
            worldRenderer.pos(this.posX + f, this.posY + (float) (glyphPage.getMaxFontHeight() / 2) + 3, 0.0D)
                    .endVertex();
            worldRenderer.pos(this.posX + f, this.posY + (float) (glyphPage.getMaxFontHeight() / 2) - 2, 0.0D)
                    .endVertex();
            worldRenderer.pos(this.posX, this.posY + (float) (glyphPage.getMaxFontHeight() / 2) - 2, 0.0D).endVertex();
            tessellator.draw();
            GlStateManager.enableTexture2D();
        }

        if (this.underlineStyle) {
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldRenderer = tessellator.getWorldRenderer();
            GlStateManager.disableTexture2D();
            GlStateManager.color(r, g, b, alpha);
            worldRenderer.begin(7, DefaultVertexFormats.POSITION);
            int l = this.underlineStyle ? -1 : 0;
            worldRenderer.pos(this.posX + (float) l, this.posY + (float) glyphPage.getMaxFontHeight() - 1F, 0.0D)
                    .endVertex();
            worldRenderer.pos(this.posX + f, this.posY + (float) glyphPage.getMaxFontHeight() - 1F, 0.0D).endVertex();
            worldRenderer.pos(this.posX + f, this.posY + (float) glyphPage.getMaxFontHeight() - 6F, 0.0D).endVertex();
            worldRenderer.pos(this.posX + (float) l, this.posY + (float) glyphPage.getMaxFontHeight() - 6F, 0.0D)
                    .endVertex();
            tessellator.draw();
            GlStateManager.enableTexture2D();
        }

        this.posX += f;
    }

    private GlyphPage getCurrentGlyphPage() {
        if (boldStyle && italicStyle)
            return pageBoldItalic;
        else if (boldStyle)
            return pageBold;
        else if (italicStyle)
            return pageItalic;
        else
            return pageRegular;
    }

    private GlyphPage getCurrentRealGlyphPage() {
        if (boldStyle && italicStyle)
            return unscaledPageBoldItalic;
        else if (boldStyle)
            return unscaledPageBold;
        else if (italicStyle)
            return unscaledPageItalic;
        else
            return unscaledPageRegular;
    }

    /**
     * Reset all style flag fields in the class to false; called at the start of string rendering
     */
    private void resetStyles() {
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
    public String trimStringToWidth(String text, int width) {
        return this.trimStringToWidth(text, width, false);
    }

    /**
     * Trims a string to a specified width, and will reverse it if par3 is set.
     */
    @Override
    public String trimStringToWidth(String text, int maxWidth, boolean reverse) {
        StringBuilder stringbuilder = new StringBuilder();

        boolean colorCodeActivated = false;

        int startIndex = reverse ? text.length() - 1 : 0;
        int step = reverse ? -1 : 1;

        float actualWidth = 0.0F;

        for (int i = startIndex; i >= 0 && i < text.length(); i += step) {
            char character = text.charAt(i);

            if (character == '§')
                colorCodeActivated = true;
            else if (colorCodeActivated && character >= '0' && character <= 'r') {
                updateStyle(character);
                i++;
                colorCodeActivated = false;
            } else if (colorCodeActivated) {
                i--;
                character = text.charAt(i);
            }

            actualWidth += getCharWidthFloat(character);
            if (actualWidth >= maxWidth)
                break;

            if (reverse)
                stringbuilder.insert(0, character);
            else
                stringbuilder.append(character);
        }

        return stringbuilder.toString();
    }

    /**
     * Breaks a string into a list of pieces that will fit a specified width.
     *
     * @param text  The text
     * @param width The target width
     * @return The list of broken strings
     */
    @Override
    public List<String> listFormattedStringToWidth(final String text, final int width) {
        return Arrays.asList(this.wrapFormattedStringToWidth(text, width).split("\n"));
    }

    /**
     * Inserts newline and formatting into a string to wrap it within the specified width.
     *
     * @param text  The text
     * @param width The target width
     * @return The string with new lines determined via \n
     */
    @Override
    public String wrapFormattedStringToWidth(final String text, final int width) {
        int i = this.sizeStringToWidth(text, width);

        if (text.length() <= i) {
            return text;
        } else {
            String s = text.substring(0, i);
            char c0 = text.charAt(i);
            boolean flag = c0 == 32 || c0 == 10;
            String s1 = FontRenderer.getFormatFromString(s) + text.substring(i + (flag ? 1 : 0));
            return s + "\n" + this.wrapFormattedStringToWidth(s1, width);
        }
    }

    /**
     * Determines how many characters from the string will fit into the specified width.
     *
     * @param text  The text
     * @param width The target width
     * @return The amount of characters
     */
    @Override
    public int sizeStringToWidth(final String text, final int width) {
        int i = text.length();
        float f = 0.0F;
        int j = 0;
        int k = -1;

        for (boolean flag = false; j < i; ++j) {
            char c0 = text.charAt(j);

            switch (c0) {
                case '\n':
                    --j;
                    break;

                case ' ':
                    k = j;

                default:
                    f += this.getCharWidthFloat(c0);

                    if (flag) {
                        ++f;
                    }

                    break;

                case '\u00a7':
                    if (j < i - 1) {
                        ++j;
                        char c1 = text.charAt(j);

                        if (c1 != 108 && c1 != 76) {
                            if (c1 == 114 || c1 == 82 || FontRenderer.isFormatColor(c1)) {
                                flag = false;
                            }
                        } else {
                            flag = true;
                        }
                    }
            }

            if (c0 == 10) {
                ++j;
                k = j;
                break;
            }

            if (f > (float) width) {
                break;
            }
        }

        return j != i && k != -1 && k < j ? k : j;
    }

    @Override
    public String toString() {
        return "GlyphFontRenderer{" +
                "family=" + pageRegular.getFont().getFamily() +
                ", size=" + pageRegular.getFont().getSize() +
                '}';
    }
}