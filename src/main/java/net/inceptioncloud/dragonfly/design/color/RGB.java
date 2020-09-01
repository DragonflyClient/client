package net.inceptioncloud.dragonfly.design.color;

import java.awt.*;

/**
 * This utility class allows the easy transformation of color values.
 */
public class RGB {
    /**
     * The color that is being transformed.
     */
    private Color color;

    /**
     * Constructor with RGB.
     *
     * @param rgb The RGB integer value
     */
    private RGB(int rgb) {
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
    private RGB(int r, int g, int b, int a) {
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
    private RGB(float r, float g, float b, float a) {
        color = new Color(r, g, b, a);
    }

    /**
     * @see RGB#RGB(int) Constructor that will be called
     */
    public static RGB of(int rgb) {
        return new RGB(rgb);
    }

    /**
     * @see RGB#RGB(int, int, int, int) Constructor that will be called
     */
    public static RGB of(int r, int g, int b, int a) {
        return new RGB(r, g, b, a);
    }

    /**
     * @see RGB#RGB(float, float, float, float) Constructor that will be called
     */
    public static RGB of(float r, float g, float b, float a) {
        return new RGB(r, g, b, a);
    }

    /**
     * @see RGB#RGB(int)  Constructor that will be called
     */
    public static RGB of(Color color) {
        return new RGB(color.getRGB());
    }

    /**
     * Transform the alpha value of the color.
     *
     * @param alpha The integer alpha
     */
    public RGB alpha(int alpha) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        color = new Color(r, g, b, Math.min(alpha, 255));
        return this;
    }

    /**
     * Transform the alpha value of the color.
     *
     * @param alpha The float alpha
     */
    public RGB alpha(float alpha) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        color = new Color(r / 255F, g / 255F, b / 255F, Math.min(alpha, 1.0F));
        return this;
    }

    /**
     * Transform the alpha value of the color.
     *
     * @param percent The percent of the alpha value
     */
    public RGB modifyAlphaRelative(float percent) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int a = color.getAlpha();

        color = new Color(r / 255F, g / 255F, b / 255F, (a / 255F) * percent);
        return this;
    }

    /**
     * @return The RGB value of the color
     */
    public int rgb() {
        return color.getRGB();
    }

    /**
     * @return The AWT color.
     */
    public Color toColor() {
        return color;
    }
}
