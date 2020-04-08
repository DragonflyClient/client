package net.inceptioncloud.minecraftmod.discord.custom;

import net.inceptioncloud.minecraftmod.discord.RichPresenceAdapter;

/**
 * Dispalyed when the user is in on a multiplayer server.
 */
public class MultiplayerRPC extends RichPresenceAdapter
{
    public MultiplayerRPC (String server, boolean paused, long joinTime)
    {
        setStartMillis(joinTime);
        setTitle(paused ? "Multiplayer - Paused" : "Multiplayer");
        setExtra("on " + server);
    }
}
