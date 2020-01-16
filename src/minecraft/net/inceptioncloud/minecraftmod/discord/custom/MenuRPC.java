package net.inceptioncloud.minecraftmod.discord.custom;

import net.inceptioncloud.minecraftmod.discord.RichPresenceStatus;

/**
 * Displayed when the user is in an out-game menu.
 */
public class MenuRPC extends RichPresenceStatus
{
    public MenuRPC ()
    {
        setStartMillis(System.currentTimeMillis());
        setExtra("Menu");
    }
}
