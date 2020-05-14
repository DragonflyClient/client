package net.inceptioncloud.minecraftmod.discord;

import net.arikia.dev.drpc.*;
import net.inceptioncloud.minecraftmod.Dragonfly;
import net.inceptioncloud.minecraftmod.discord.custom.MenuRPC;
import net.inceptioncloud.minecraftmod.discord.subscriber.RichPresenceSubscriber;
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
     * The current rich presence status.
     */
    private RichPresenceAdapter status;

    /**
     * Initialized when loading the {@link Dragonfly}.
     */
    public RichPresenceManager ()
    {
        LogManager.getLogger().info("Enabling Discord Rich Presence...");
        Dragonfly.getEventBus().register(new RichPresenceSubscriber());

        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder()
            .setReadyEventHandler(discordUser -> LogManager.getLogger().info("Discord Rich Presence is ready!"))
            .build();

        DiscordRPC.discordInitialize("696368023333765120", handlers, true);

        // KeepAlive-Thread
        new Thread(() ->
        {
            while (open)
                DiscordRPC.discordRunCallbacks();
        }, "Discord RPC Callback").start();

        update(new MenuRPC());
    }

    /**
     * Update the Discord Rich Presence with a {@link RichPresenceAdapter}.
     *
     * @param status The status
     */
    public void update (RichPresenceAdapter status)
    {
        this.status = status;
        update(status.buildRichPresence());
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

    public boolean isOpen ()
    {
        return open;
    }

    public RichPresenceAdapter getStatus ()
    {
        return status;
    }
}
