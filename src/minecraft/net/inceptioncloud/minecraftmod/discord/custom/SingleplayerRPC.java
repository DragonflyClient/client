package net.inceptioncloud.minecraftmod.discord.custom;

import net.inceptioncloud.minecraftmod.discord.RichPresenceAdapter;

/**
 * Dispalyed when the user is in a singleplayer world.
 */
public class SingleplayerRPC extends RichPresenceAdapter
{
    public SingleplayerRPC (String world, boolean paused, long joinTime)
    {
        setStartMillis(joinTime);
        setTitle(paused ? "Singleplayer - Paused" : "Singleplayer");
        setExtra("in " + world);
    }
}
