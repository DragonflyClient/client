package net.inceptioncloud.minecraftmod.design.font;

import lombok.*;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to build new Font Renderers or use the already created ones.
 */
public class FontManager
{
    /**
     * Cache with all previously built Font Renderers.
     */
    private final Map<FontRendererInfo, GlyphFontRenderer> cache = new HashMap<>();

    /**
     * Build a new Custom Font Renderer or use an already created one.
     *
     * @param name The font name
     * @param type The font type (bold, plain, ..)
     * @param size The font size
     *
     * @return The Font Renderer
     */
    public IFontRenderer retrieveOrBuild (String name, int type, int size, double letterSpacing)
    {
        final FontRendererInfo info = new FontRendererInfo(name, type, size, letterSpacing);

        if (cache.containsKey(info))
            return cache.get(info);

        final GlyphFontRenderer fontRenderer = GlyphFontRenderer.create(info.name, info.size, info.letterSpacing,info.type == Font.BOLD, info.type == Font.ITALIC, false);
        cache.put(info, fontRenderer);

        return fontRenderer;
    }

    /**
     * Convenient Method
     * @see #retrieveOrBuild(String, int, int, double) Original Method
     */
    public IFontRenderer retrieveOrBuild (String name, int type, int size)
    {
        return retrieveOrBuild(name, type, size, -0.025);
    }

    /**
     * Convenient Method
     * @see #retrieveOrBuild(String, int, int, double) Original Method
     */
    public IFontRenderer retrieveOrBuild (String extra, int size)
    {
        return retrieveOrBuild(getFont() + extra, Font.BOLD, size);
    }

    /**
     * @return Currently used Font
     */
    public String getFont ()
    {
        return "Rubik";
    }

    /**
     * Applies a certain letter spacing to the font.
     */
    public static Font applyLetterSpacing (Font font, double spacing)
    {
        Map<TextAttribute, Double> attributes = new HashMap<>();
        attributes.put(TextAttribute.TRACKING, spacing);
        return font.deriveFont(attributes);
    }

    /**
     * Clears the Font Renderer cache.
     */
    public void clearCache ()
    {
        this.cache.clear();
    }

    /**
     * A custom Font Renderer with the "Product Sans" font and a size of 19.
     */
    public IFontRenderer getRegular ()
    {
        return retrieveOrBuild("", 19);
    }

    /**
     * A custom Font Renderer with the "Product Sans Medium" font and a size of 19.
     */
    public IFontRenderer getMedium ()
    {
        return retrieveOrBuild(" Medium", 19);
    }

    /**
     * A custom Font Renderer for rendering the ingame title with the "Product Sans Medium" font and a size of 38.
     */
    public IFontRenderer getTitle ()
    {
        return retrieveOrBuild(" Medium", 38);
    }

    /**
     * A custom Font Renderer for rendering the ingame subtitle with the "Product Sans Medium" font and a size of 22.
     */
    public IFontRenderer getSubtitle ()
    {
        return retrieveOrBuild(" Medium", 22);
    }

    /**
     * Contains information about the Font Renderer.
     */
    @RequiredArgsConstructor
    @EqualsAndHashCode
    public static class FontRendererInfo
    {
        /**
         * The name of the font.
         */
        private final String name;

        /**
         * The font type. (bold, plain, ..)
         */
        private final int type;

        /**
         * The size of the font.
         */
        private final int size;

        /**
         * The spacing between the letters.
         */
        private final double letterSpacing;
    }
}
