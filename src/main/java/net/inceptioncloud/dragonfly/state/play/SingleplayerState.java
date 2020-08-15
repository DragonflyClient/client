package net.inceptioncloud.dragonfly.state.play;

import net.inceptioncloud.dragonfly.discord.RichPresenceAdapter;
import net.inceptioncloud.dragonfly.discord.custom.SingleplayerRPC;
import net.minecraft.client.Minecraft;
import net.minecraft.server.integrated.IntegratedServer;

import java.util.Objects;

/**
 * When the player is in a singleplayer world.
 */
public class SingleplayerState extends PlayingState {
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
    public SingleplayerState(final boolean paused, final long joinTime, final IntegratedServer integratedServer) {
        super(paused, joinTime);
        this.integratedServer = integratedServer;
    }

    /**
     * @return The {@link RichPresenceAdapter} that belongs to this Game State.
     */
    @Override
    public RichPresenceAdapter getBelongingRichPresence() {
        return new SingleplayerRPC(integratedServer.getWorldName(), isPaused(), getJoinTime());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final SingleplayerState that = (SingleplayerState) o;
        return Objects.equals(integratedServer, that.integratedServer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), integratedServer);
    }

    public IntegratedServer getIntegratedServer() {
        return integratedServer;
    }

    @Override
    public String toString() {
        return "SingleplayerState{" +
                "integratedServer=" + integratedServer +
                '}';
    }
}
