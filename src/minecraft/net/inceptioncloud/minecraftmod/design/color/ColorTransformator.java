package net.inceptioncloud.minecraftmod.design.color;

import java.awt.*;

/**
 * This utility class allows the easy transformation of color values.
 */
public class ColorTransformator
{
    /**
     * The color that is being transformed.
     */
    private Color color;

    /**
     * Constructor with RGB.
     *
     * @param rgb The RGB integer value
     */
    private ColorTransformator (int rgb)
    {
        color = new Color(rgb);
    }

    /**
     * Constructor with R, G, B, A.
     *
     * @param r The red integer value
     * @param g The green integer value
     * @param b The blue integer value
     * @param a The alpha integer value
     */
    private ColorTransformator (int r, int g, int b, int a)
    {
        color = new Color(r, g, b, a);
    }

    /**
     * Constructor with R, G, B, A.
     *
     * @param r The red float value
     * @param g The green float value
     * @param b The blue float value
     * @param a The alpha float value
     */
    private ColorTransformator (float r, float g, float b, float a)
    {
        color = new Color(r, g, b, a);
    }

    /**
     * @see ColorTransformator#ColorTransformator(int) Constructor that will be called
     */
    public static ColorTransformator of (int rgb)
    {
        return new ColorTransformator(rgb);
    }

    /**
     * @see ColorTransformator#ColorTransformator(int, int, int, int) Constructor that will be called
     */
    public static ColorTransformator of (int r, int g, int b, int a)
    {
        return new ColorTransformator(r, g, b, a);
    }

    /**
     * @see ColorTransformator#ColorTransformator(float, float, float, float) Constructor that will be called
     */
    public static ColorTransformator of (float r, float g, float b, float a)
    {
        return new ColorTransformator(r, g, b, a);
    }

    /**
     * @see ColorTransformator#ColorTransformator(int)  Constructor that will be called
     */
    public static ColorTransformator of (Color color)
    {
        return new ColorTransformator(color.getRGB());
    }

    /**
     * Transform the alpha value of the color.
     *
     * @param alpha The integer alpha
     */
    public ColorTransformator transformAlpha (int alpha)
    {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        color = new Color(r, g, b, alpha);
        return this;
    }

    /**
     * Transform the alpha value of the color.
     *
     * @param alpha The float alpha
     */
    public ColorTransformator transformAlpha (float alpha)
    {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        color = new Color(r / 255F, g / 255F, b / 255F, alpha);
        return this;
    }

    /**
     * @return The RGB value of the color
     */
    public int toRGB ()
    {
        return color.getRGB();
    }
}
