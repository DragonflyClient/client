package net.inceptioncloud.dragonfly.event.play;

import net.inceptioncloud.dragonfly.event.*;
import net.minecraft.client.multiplayer.ServerData;

/**
 * When a player did successfully login to a server.
 */
public class ServerLoggedInEvent extends Cancellable implements Event {
    /**
     * The {@link ServerData} object of the server he is connecting to.
     */
    private final ServerData serverData;

    public ServerLoggedInEvent(final ServerData serverData) {
        this.serverData = serverData;
    }

    public ServerData getServerData() {
        return serverData;
    }
}
