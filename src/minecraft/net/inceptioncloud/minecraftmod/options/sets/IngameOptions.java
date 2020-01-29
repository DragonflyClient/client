package net.inceptioncloud.minecraftmod.options.sets;

import net.inceptioncloud.minecraftmod.options.OptionKey;

/**
 * Contains all prepared {@link OptionKey}s for in-game options.
 */
public class IngameOptions
{
    public static final OptionKey<Boolean> ENABLE_ZOOM_ANIMATION = OptionKey.newInstance(Boolean.class).key("enableZoomAnimation").defaultValue(Boolean.TRUE).validator(val -> true).build();
}
