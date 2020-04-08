package net.inceptioncloud.minecraftmod.tracking.transitions

import com.google.common.eventbus.Subscribe
import net.inceptioncloud.minecraftmod.event.client.ClientShutdownEvent
import net.inceptioncloud.minecraftmod.tracking.transitions.TransitionTracker.generateFile

/**
 * Listens to the Client Shutdown Event to save the Tracking File.
 */
class FileSaveSubscriber
{
    /**
     * [ClientShutdownEvent] Subscriber
     */
    @Subscribe
    fun clientShutdown(event: ClientShutdownEvent?)
    {
        generateFile()
    }
}