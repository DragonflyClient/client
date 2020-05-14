package net.inceptioncloud.minecraftmod.state;

import net.inceptioncloud.minecraftmod.Dragonfly;
import net.inceptioncloud.minecraftmod.event.client.GameStateUpdateEvent;
import net.inceptioncloud.minecraftmod.state.menu.subscriber.MenuSubscriber;
import net.inceptioncloud.minecraftmod.state.play.subscriber.MultiplayerSubscriber;
import net.inceptioncloud.minecraftmod.state.play.subscriber.SingleplayerSubscriber;
import org.apache.logging.log4j.LogManager;

/**
 * Holds the current Game State and allows to change it.
 */
public class GameStateManager
{
    /**
     * The current Game State.
     */
    private GameState current = new UnknownGameState();

    /**
     * Default Constructor
     */
    public GameStateManager ()
    {
        Dragonfly.getEventBus()
            .registerAnd(new SingleplayerSubscriber())
            .registerAnd(new MultiplayerSubscriber())
            .register(new MenuSubscriber());
    }

    /**
     * @return The current Game State.
     */
    public GameState getCurrent ()
    {
        return current;
    }

    /**
     * Change the current Game State to the new one.
     *
     * @param newState The new Game State
     */
    public void updateState (final GameState newState)
    {
        // EVENTBUS - Calls the GameStateUpdateEvent when the State is updated
        GameStateUpdateEvent event = new GameStateUpdateEvent(current, newState);
        Dragonfly.getEventBus().post(event);

        LogManager.getLogger().info("[Game State] " + newState);

        this.current = newState;
    }
}
