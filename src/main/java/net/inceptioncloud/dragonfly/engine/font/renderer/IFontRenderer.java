package net.inceptioncloud.dragonfly.engine.font.renderer;

import net.minecraft.client.gui.FontRenderer;

import java.util.List;

/**
 * The parent interface of the Minecraft {@link FontRenderer} and the InceptionCloud {@link GlyphFontRenderer} and {@link UnicodeFontRenderer}.
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
     * Draws the specified string with a shadow that can have a custom color and distance.
     */
    int drawStringWithCustomShadow(String text, int x, int y, int color, int shadowColor, float distance);

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
     * The exact with of the specific char in the current font.
     *
     * @param c The character
     *
     * @return The width in pixels
     */
    float getCharWidthFloat (char c);

    /**
     * @return The default character height
     */
    int getHeight ();

    /**
     * Trims the given string to be equal or less wide than the given width.
     *
     * @param text  The text
     * @param width The target with
     *
     * @return The trimmed string
     */
    String trimStringToWidth (String text, int width);

    /**
     * Trims the given string to be equal or less wide than the given width.
     *
     * @param text    The text
     * @param width   The target with
     * @param reverse Whether to reverse the string
     *
     * @return The trimmed string
     */
    String trimStringToWidth (String text, int width, boolean reverse);

    /**
     * Breaks a string into a list of pieces that will fit a specified width.
     *
     * @param text  The text
     * @param width The target width
     *
     * @return The list of broken strings
     */
    List<String> listFormattedStringToWidth (String text, int width);

    /**
     * Inserts newline and formatting into a string to wrap it within the specified width.
     *
     * @param text  The text
     * @param width The target width
     *
     * @return The string with new lines determined via \n
     */
    String wrapFormattedStringToWidth (String text, int width);

    /**
     * Determines how many characters from the string will fit into the specified width.
     *
     * @param text  The text
     * @param width The target width
     *
     * @return The amount of characters
     */
    int sizeStringToWidth (String text, int width);

    /**
     * The default implementation for drawing a string centered on the screen.
     *
     * @param text   The string to be drawn
     * @param x      The x location of the center
     * @param y      The y location
     * @param color  The color in which the text is drawn
     * @param shadow Whether to draw a shadow or not
     *
     * @return The end x-value
     */
    default int drawCenteredString (String text, int x, int y, int color, boolean shadow)
    {
        int absoluteX = x - ( getStringWidth(text) / 2 );
        return drawString(text, absoluteX, y, color, shadow);
    }
}
