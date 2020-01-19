package net.inceptioncloud.minecraftmod.discord.subscriber;

import com.google.common.eventbus.Subscribe;
import net.inceptioncloud.minecraftmod.InceptionMod;
import net.inceptioncloud.minecraftmod.event.structure.GameStateUpdateEvent;

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
        InceptionMod.getInstance().getRichPresenceManager().update(event.getNext().getBelongingRichPresence());
    }
}
