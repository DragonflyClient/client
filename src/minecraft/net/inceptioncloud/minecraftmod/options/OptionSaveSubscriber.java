package net.inceptioncloud.minecraftmod.options;

import com.google.common.eventbus.Subscribe;
import net.inceptioncloud.minecraftmod.Dragonfly;
import net.inceptioncloud.minecraftmod.event.client.ClientShutdownEvent;

/**
 * Saves the options when shutting down the client (via {@link ClientShutdownEvent}).
 */
public class OptionSaveSubscriber
{
    /**
     * {@link ClientShutdownEvent} Subscriber
     */
    @Subscribe
    public void clientShutdown (ClientShutdownEvent event)
    {
        Dragonfly.getOptions().contentSave();
    }
}
