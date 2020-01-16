package net.inceptioncloud.minecraftmod.discord.custom;

import net.inceptioncloud.minecraftmod.discord.RichPresenceStatus;

/**
 * The Rich Presence that is displayed when loading the Mod.
 */
public class LoadingModRPC extends RichPresenceStatus
{
    /**
     * Default Constructor.
     */
    public LoadingModRPC ()
    {
        setStartMillis(System.currentTimeMillis());
        setExtra("Loading Mod...");
    }
}
