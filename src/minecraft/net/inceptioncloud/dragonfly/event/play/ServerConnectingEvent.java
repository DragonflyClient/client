package net.inceptioncloud.dragonfly.event.play;

import net.inceptioncloud.dragonfly.event.Cancellable;

/**
 * When a player is trying to connect to a server.
 */
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

    public ServerConnectingEvent (final String ipAddress, final int port)
    {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public String getIpAddress ()
    {
        return ipAddress;
    }

    public int getPort ()
    {
        return port;
    }
}
