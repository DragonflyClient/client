package net.inceptioncloud.dragonfly.discord.subscriber;

import com.google.common.eventbus.Subscribe;
import net.inceptioncloud.dragonfly.Dragonfly;
import net.inceptioncloud.dragonfly.event.client.GameStateUpdateEvent;

/**
 * The subscriber that handles the updating of the Discord Rich Presence when changing
 * the Game State.
 */
public class RichPresenceSubscriber
{
    /**
     * Updates the Rich Presence to the one that belongs to the new Game State.
     */
    @Subscribe
    public void gameStateUpdate (GameStateUpdateEvent event)
    {
        Dragonfly.getRichPresenceManager().update(event.getNext().getBelongingRichPresence());
    }
}
