package net.inceptioncloud.minecraftmod.state.play;

import lombok.*;
import net.inceptioncloud.minecraftmod.state.GameState;

/**
 * The superclass of any Game State in which the user is playing.
 * These are {@link SingleplayerState} and {@link MultiplayerState}.
 */
@Getter
@ToString
@EqualsAndHashCode ( callSuper = true )
public abstract class PlayingState extends GameState
{
    /**
     * Whether the game is currently paused.
     */
    private boolean paused;

    /**
     * The time (in millis) when the user joined in-game.
     */
    private final long joinTime;

    /**
     * Default Constructor
     */
    PlayingState (final boolean paused, final long joinTime)
    {
        this.paused = paused;
        this.joinTime = joinTime;
    }
}
