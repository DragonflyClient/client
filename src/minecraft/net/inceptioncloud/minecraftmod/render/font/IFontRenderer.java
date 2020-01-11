package net.inceptioncloud.minecraftmod.render.font;

import net.minecraft.client.gui.FontRenderer;

/**
 * The parent interface of the Minecraft {@link FontRenderer} and the InceptionCloud {@link CustomFontRenderer}.
 */
public interface IFontRenderer
{
    /**
     * Draw a left-justified string at the given location with a specific color.
     *
     * @see #drawString(String, float, float, int, boolean) Parameter Description
     */
    int drawString (String text, int x, int y, int color);

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
    int drawString (String text, float x, float y, int color, boolean dropShadow);

    /**
     * Draw a left-justified string at the given location with a specific color and
     * a shadow.
     *
     * @see #drawString(String, float, float, int, boolean) Parameter Description
     */
    int drawStringWithShadow (String text, float x, float y, int color);

    /**
     * Get the width of a string in the current font.
     *
     * @param text The text
     *
     * @return The width in pixels
     */
    int getStringWidth (String text);

    /**
     * @param c The character
     *
     * @return {@link #getCharWidthFloat(char)} rounded to an integer value.
     */
    int getCharWidth (char c);

    /**
     * @return The default character height
     */
    int getHeight ();

    /**
     * The exact with of the specific char in the current font.
     *
     * @param c The character
     *
     * @return The width in pixels
     */
    float getCharWidthFloat (char c);

    /**
     * Trims the given string to be equal or less wide than the given width.
     *
     * @param text  The text
     * @param width The target with
     *
     * @return The trimmed string
     */
    String trimStringToWidth (String text, int width);
}
