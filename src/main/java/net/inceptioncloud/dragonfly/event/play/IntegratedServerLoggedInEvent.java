package net.inceptioncloud.dragonfly.event.play;

import net.minecraft.client.Minecraft;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.WorldSettings;

/**
 * When an integrated server was successfully started by {@link Minecraft#launchIntegratedServer(String, String, WorldSettings)}.
 */
public class IntegratedServerLoggedInEvent
{
    /**
     * The integrated server that has been launched.
     */
    private final IntegratedServer integratedServer;

    public IntegratedServerLoggedInEvent (final IntegratedServer integratedServer)
    {
        this.integratedServer = integratedServer;
    }

    public IntegratedServer getIntegratedServer ()
    {
        return integratedServer;
    }
}
