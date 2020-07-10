package net.inceptioncloud.dragonfly.versioning.updater

import com.google.common.eventbus.Subscribe
import net.inceptioncloud.dragonfly.event.client.ApplicationStartEvent

/**
 * Listens for the client startup event and launches the updater if necessary.
 */
class ApplicationStartSubscriber {

    @Subscribe
    fun clientStartup(event: ApplicationStartEvent) {
        if (AutoUpdater.isUpdateAvailable()) {
            event.isCancelled = true
            AutoUpdater.update()
        }
    }
}