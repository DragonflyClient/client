package net.inceptioncloud.minecraftmod.subscriber;

import com.google.common.eventbus.Subscribe;
import net.inceptioncloud.minecraftmod.event.play.ServerLoggedInEvent;
import net.inceptioncloud.minecraftmod.options.sets.StorageOptions;
import net.minecraft.client.multiplayer.ServerData;
import org.apache.logging.log4j.LogManager;

/**
 * Listens to the {@link ServerLoggedInEvent} and saves the last server.
 */
public class LastServerSaveSubscriber
{
    /**
     * {@link ServerLoggedInEvent} Subscriber
     */
    @Subscribe
    public void serverLoggedIn (ServerLoggedInEvent event)
    {
        if (event.getServerData().isLan())
            return;

        final String serverIP = event.getServerData().serverIP;
        final String serverName = event.getServerData().serverName.equals("Minecraft Server") ? serverIP : event.getServerData().serverName;

        StorageOptions.LAST_SERVER.set(new ServerData(serverName, serverIP, false));
        LogManager.getLogger().info("Stored last visited server {} called {}.", serverIP, serverName);
    }
}
