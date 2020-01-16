package net.inceptioncloud.minecraftmod.discord;

import net.arikia.dev.drpc.*;
import net.inceptioncloud.minecraftmod.MinecraftMod;
import net.inceptioncloud.minecraftmod.discord.custom.LoadingModRPC;
import org.apache.logging.log4j.LogManager;

/**
 * Manages the updating and displaying of the Discord Rich Presence.
 */
public class RichPresenceManager
{
    /**
     * Whether the Rich Presence Channel is open.
     */
    private boolean open = true;

    /**
     * Initialized when loading the {@link MinecraftMod}.
     */
    public RichPresenceManager ()
    {
        LogManager.getLogger().info("Enabling Discord Rich Presence...");

        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler(discordUser -> LogManager.getLogger().info("Discord Rich Presence is ready!")).build();
        DiscordRPC.discordInitialize("667006162910052352", handlers, true);

        // KeepAlive-Thread
        new Thread(() ->
        {
            while (open)
                DiscordRPC.discordRunCallbacks();
        }, "Discord RPC Callback").start();

        update(new LoadingModRPC().buildRichPresence());
    }

    /**
     * Update the currently displayed Rich Presence.
     *
     * @param richPresence The Rich Presence instance
     */
    public void update (DiscordRichPresence richPresence)
    {
        DiscordRPC.discordUpdatePresence(richPresence);
    }

    /**
     * End the connection to Discord and remove the Rich Presence.
     */
    public void close ()
    {
        open = false;
        DiscordRPC.discordShutdown();
    }
}
