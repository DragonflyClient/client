package net.inceptioncloud.dragonfly.versioning.updater

import com.google.common.eventbus.Subscribe
import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.event.client.ApplicationStartEvent
import org.apache.logging.log4j.LogManager

/**
 * Listens for the client startup event and launches the updater if necessary.
 */
class ApplicationStartSubscriber {

    @Subscribe
    fun clientStartup(event: ApplicationStartEvent) {
        if (event.isDeveloperMode) {
            LogManager.getLogger().info("Skipping auto updater since Dragonfly is in developer mode!")
            return
        }

        if (AutoUpdater.isUpdateAvailable()) {
            event.isCancelled = true
            AutoUpdater.update()
        }
    }
}