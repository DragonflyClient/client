package net.inceptioncloud.dragonfly.state.play;

import net.inceptioncloud.dragonfly.state.GameState;

import java.util.Objects;

/**
 * The superclass of any Game State in which the user is playing.
 * These are {@link SingleplayerState} and {@link MultiplayerState}.
 */
public abstract class PlayingState extends GameState {
    /**
     * Whether the game is currently paused.
     */
    private final boolean paused;

    /**
     * The time (in millis) when the user joined in-game.
     */
    private final long joinTime;

    /**
     * Default Constructor
     */
    PlayingState(final boolean paused, final long joinTime) {
        this.paused = paused;
        this.joinTime = joinTime;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PlayingState that = (PlayingState) o;
        return paused == that.paused &&
                joinTime == that.joinTime;
    }

    @Override
    public String toString() {
        return "PlayingState{" +
                "paused=" + paused +
                ", joinTime=" + joinTime +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(paused, joinTime);
    }

    public boolean isPaused() {
        return paused;
    }

    public long getJoinTime() {
        return joinTime;
    }
}
