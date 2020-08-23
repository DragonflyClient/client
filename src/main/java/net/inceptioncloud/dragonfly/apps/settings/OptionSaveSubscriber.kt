package net.inceptioncloud.dragonfly.apps.settings

import com.google.common.eventbus.Subscribe
import net.inceptioncloud.dragonfly.apps.settings.DragonflyOptions
import net.inceptioncloud.dragonfly.event.client.ClientShutdownEvent

/**
 * Saves the options when shutting down the client (via [ClientShutdownEvent]).
 */
object OptionSaveSubscriber {

    /**
     * [ClientShutdownEvent] Subscriber
     */
    @Subscribe
    fun clientShutdown(event: ClientShutdownEvent?) {
        DragonflyOptions.contentSave()
    }
}