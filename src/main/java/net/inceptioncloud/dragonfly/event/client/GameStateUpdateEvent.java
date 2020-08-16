package net.inceptioncloud.dragonfly.event.client;

import net.inceptioncloud.dragonfly.state.*;

/**
 * When the current Game State is changed via {@link GameStateManager#updateState(GameState)}.
 */
public class GameStateUpdateEvent {
    /**
     * The previously selected Game State.
     */
    private final GameState previous;

    /**
     * The Game State to which the current selected is being changed.
     */
    private final GameState next;

    public GameStateUpdateEvent(final GameState previous, final GameState next) {
        this.previous = previous;
        this.next = next;
    }

    public GameState getPrevious() {
        return previous;
    }

    public GameState getNext() {
        return next;
    }
}
