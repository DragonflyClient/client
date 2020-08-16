package net.inceptioncloud.dragonfly.discord.custom;

import net.inceptioncloud.dragonfly.discord.RichPresenceAdapter;

/**
 * Dispalyed when the user is in a singleplayer world.
 */
public class SingleplayerRPC extends RichPresenceAdapter {
    public SingleplayerRPC(String world, boolean paused, long joinTime) {
        setStartMillis(joinTime);
        setTitle(paused ? "Singleplayer - Paused" : "Singleplayer");
        setExtra("in " + world);
    }
}
