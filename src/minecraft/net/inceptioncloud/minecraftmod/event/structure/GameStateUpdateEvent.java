package net.inceptioncloud.minecraftmod.event.structure;

import lombok.*;
import net.inceptioncloud.minecraftmod.state.GameState;
import net.inceptioncloud.minecraftmod.state.GameStateManager;

/**
 * When the current Game State is changed via {@link GameStateManager#updateState(GameState)}.
 */
@Getter
@RequiredArgsConstructor
public class GameStateUpdateEvent
{
    /**
     * The previously selected Game State.
     */
    private final GameState previous;

    /**
     * The Game State to which the current selected is being changed.
     */
    private final GameState next;
}
