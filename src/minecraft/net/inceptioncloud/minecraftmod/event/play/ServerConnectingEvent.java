package net.inceptioncloud.minecraftmod.event.play;

import lombok.*;
import net.inceptioncloud.minecraftmod.event.Cancellable;

/**
 * When a player is trying to connect to a server.
 */
@Getter
@RequiredArgsConstructor
public class ServerConnectingEvent extends Cancellable
{
    /**
     * The IP address of the server.
     */
    private final String ipAddress;

    /**
     * The port of the server.
     */
    private final int port;
}
