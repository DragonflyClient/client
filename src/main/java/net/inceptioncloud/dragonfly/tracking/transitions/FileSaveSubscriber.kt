package net.inceptioncloud.dragonfly.tracking.transitions

import com.google.common.eventbus.Subscribe
import net.inceptioncloud.dragonfly.event.client.ClientShutdownEvent
import net.inceptioncloud.dragonfly.tracking.transitions.TransitionTracker.generateFile

/**
 * Listens to the Client Shutdown Event to save the Tracking File.
 */
class FileSaveSubscriber {
    /**
     * [ClientShutdownEvent] Subscriber
     */
    @Subscribe
    fun clientShutdown(event: ClientShutdownEvent?) {
        generateFile()
    }
}