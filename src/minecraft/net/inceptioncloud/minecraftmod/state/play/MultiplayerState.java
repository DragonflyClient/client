package net.inceptioncloud.minecraftmod.state.play;

import lombok.*;
import net.inceptioncloud.minecraftmod.discord.RichPresenceAdapter;
import net.inceptioncloud.minecraftmod.discord.custom.MultiplayerRPC;
import net.minecraft.client.multiplayer.ServerData;

/**
 * When the player is in a singleplayer world.
 */
@Getter
@ToString (callSuper = true)
@EqualsAndHashCode ( callSuper = true )
public class MultiplayerState extends PlayingState
{
    /**
     * The data of the server to which the user is connected.
     */
    private final ServerData serverData;

    /**
     * Default Constructor
     *
     * @param serverData The data of the server on which the user is playing
     */
    public MultiplayerState (final boolean paused, final long joinTime, final ServerData serverData)
    {
        super(paused, joinTime);
        this.serverData = serverData;
    }

    /**
     * @return The {@link RichPresenceAdapter} that belongs to this Game State.
     */
    @Override
    public RichPresenceAdapter getBelongingRichPresence ()
    {
        return new MultiplayerRPC(serverData.serverIP, isPaused(), getJoinTime());
    }
}
