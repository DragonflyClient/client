package net.inceptioncloud.minecraftmod.state.play;

import net.inceptioncloud.minecraftmod.discord.RichPresenceAdapter;
import net.inceptioncloud.minecraftmod.discord.custom.MultiplayerRPC;
import net.minecraft.client.multiplayer.ServerData;

import java.util.Objects;

/**
 * When the player is in a singleplayer world.
 */
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

    @Override
    public String toString ()
    {
        return "MultiplayerState{" +
               "serverData=" + serverData +
               '}';
    }

    @Override
    public boolean equals (final Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final MultiplayerState that = (MultiplayerState) o;
        return Objects.equals(serverData, that.serverData);
    }

    @Override
    public int hashCode ()
    {
        return Objects.hash(super.hashCode(), serverData);
    }

    public ServerData getServerData ()
    {
        return serverData;
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
