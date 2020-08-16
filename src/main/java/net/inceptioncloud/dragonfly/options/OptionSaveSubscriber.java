package net.inceptioncloud.dragonfly.options;

import com.google.common.eventbus.Subscribe;
import net.inceptioncloud.dragonfly.event.client.ClientShutdownEvent;

/**
 * Saves the options when shutting down the client (via {@link ClientShutdownEvent}).
 */
public class OptionSaveSubscriber {
    /**
     * {@link ClientShutdownEvent} Subscriber
     */
    @Subscribe
    public void clientShutdown(ClientShutdownEvent event) {
        Options.contentSave();
    }
}
