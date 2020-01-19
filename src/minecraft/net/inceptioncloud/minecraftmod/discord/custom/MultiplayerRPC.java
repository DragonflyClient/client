package net.inceptioncloud.minecraftmod.discord.custom;

import net.inceptioncloud.minecraftmod.discord.RichPresenceStatus;

/**
 * Dispalyed when the user is in on a multiplayer server.
 */
public class MultiplayerRPC extends RichPresenceStatus
{
    public MultiplayerRPC (String server, boolean paused, long joinTime)
    {
        setStartMillis(joinTime);
        setTitle(paused ? "Multiplayer - Paused" : "Multiplayer");
        setExtra("on " + server);
    }
}
