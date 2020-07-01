package net.inceptioncloud.dragonfly.discord.custom;

import net.inceptioncloud.dragonfly.discord.RichPresenceAdapter;

/**
 * Displayed when the user is in an out-game menu.
 */
public class MenuRPC extends RichPresenceAdapter
{
    public MenuRPC ()
    {
        setStartMillis(System.currentTimeMillis());
        setExtra("Menu");
    }
}
