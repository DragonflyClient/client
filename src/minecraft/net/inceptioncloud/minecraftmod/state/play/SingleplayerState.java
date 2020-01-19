package net.inceptioncloud.minecraftmod.state.play;

import lombok.*;
import net.inceptioncloud.minecraftmod.discord.RichPresenceStatus;
import net.inceptioncloud.minecraftmod.discord.custom.SingleplayerRPC;
import net.minecraft.client.Minecraft;
import net.minecraft.server.integrated.IntegratedServer;

/**
 * When the player is in a singleplayer world.
 */
@Getter
@ToString (callSuper = true)
@EqualsAndHashCode ( callSuper = true )
public class SingleplayerState extends PlayingState
{
    /**
     * The integrated server on which the singleplayer world runs on.
     * Equal to {@link Minecraft#getIntegratedServer()}.
     */
    private final IntegratedServer integratedServer;

    /**
     * Default Constructor
     *
     * @param integratedServer The running integrated server
     */
    public SingleplayerState (final boolean paused, final long joinTime, final IntegratedServer integratedServer)
    {
        super(paused, joinTime);
        this.integratedServer = integratedServer;
    }

    /**
     * @return The {@link RichPresenceStatus} that belongs to this Game State.
     */
    @Override
    public RichPresenceStatus getBelongingRichPresence ()
    {
        return new SingleplayerRPC(integratedServer.getWorldName(), isPaused(), getJoinTime());
    }
}
