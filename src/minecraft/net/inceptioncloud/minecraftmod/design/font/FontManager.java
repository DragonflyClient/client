package net.inceptioncloud.minecraftmod.design.font;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import net.inceptioncloud.minecraftmod.InceptionMod;

import java.awt.*;
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
    public IFontRenderer retrieveOrBuild (String name, int type, int size)
    {
        final FontRendererInfo info = new FontRendererInfo(name, type, size);

        if (cache.containsKey(info))
            return cache.get(info);

        final GlyphFontRenderer fontRenderer = GlyphFontRenderer.create(info.name, info.size, info.type == Font.BOLD, info.type == Font.ITALIC, false);
        cache.put(info, fontRenderer);

        return fontRenderer;
    }

    /**
     * A custom Font Renderer with the "Product Sans" font and a size of 19.
     */
    public IFontRenderer getRegular ()
    {
        return retrieveOrBuild("Product Sans", Font.PLAIN, 19);
    }

    /**
     * TA custom Font Renderer with the "Product Sans Medium" font and a size of 19.
     */
    public IFontRenderer getMedium ()
    {
        return retrieveOrBuild("Product Sans Medium", Font.PLAIN, 19);
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
    }
}
