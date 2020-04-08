package net.inceptioncloud.minecraftmod.discord.custom;

import net.inceptioncloud.minecraftmod.discord.RichPresenceAdapter;

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
