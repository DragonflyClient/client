package net.inceptioncloud.dragonfly.state

import net.inceptioncloud.dragonfly.Dragonfly.eventBus
import net.inceptioncloud.dragonfly.event.client.GameStateUpdateEvent
import net.inceptioncloud.dragonfly.state.menu.subscriber.MenuSubscriber
import net.inceptioncloud.dragonfly.state.play.subscriber.MultiplayerSubscriber
import net.inceptioncloud.dragonfly.state.play.subscriber.SingleplayerSubscriber
import org.apache.logging.log4j.LogManager

/**
 * Holds the current Game State and allows to change it.
 */
class GameStateManager {

    /**
     * The current Game State.
     */
    var current: GameState = UnknownGameState()
        private set

    /**
     * Change the current Game State to the new one.
     *
     * @param newState The new Game State
     */
    fun updateState(newState: GameState) {
        // EVENTBUS - Calls the GameStateUpdateEvent when the State is updated
        val event = GameStateUpdateEvent(current, newState)
        eventBus.post(event)
        current = newState
    }

    /**
     * Default Constructor
     */
    init {
        with(eventBus) {
            register(SingleplayerSubscriber())
            register(MultiplayerSubscriber())
            register(MenuSubscriber())
        }
    }
}