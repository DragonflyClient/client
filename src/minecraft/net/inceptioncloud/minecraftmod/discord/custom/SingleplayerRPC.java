package net.inceptioncloud.minecraftmod.discord.custom;

import net.inceptioncloud.minecraftmod.discord.RichPresenceStatus;

/**
 * Dispalyed when the user is in a singleplayer world.
 */
public class SingleplayerRPC extends RichPresenceStatus
{
    public SingleplayerRPC (String world, boolean paused)
    {
        setStartMillis(System.currentTimeMillis());
        setTitle(paused ? "Singleplayer - Paused" : "Singleplayer");
        setExtra("in " + world);
    }
}
