package net.inceptioncloud.dragonfly.subscriber

import net.inceptioncloud.dragonfly.event.ModEventBus
import net.inceptioncloud.dragonfly.key.StartupGuiSubscriber
import net.inceptioncloud.dragonfly.tracking.transitions.FileSaveSubscriber
import net.inceptioncloud.dragonfly.tracking.transitions.TickSubscriber

/**
 * Registers the Default Event Subscribers.
 */
object DefaultSubscribers {

    /**
     * Performs the registration.
     */
    @JvmStatic
    fun register(modEventBus: ModEventBus) {
        with(modEventBus) {
            register(AuthenticationSubscriber())
            register(FileSaveSubscriber())
            register(TickSubscriber())
            register(LastServerSaveSubscriber())
            register(StartupGuiSubscriber)
            register(DeveloperModeSubscriber)
            register(ShutdownSubscriber)
        }
    }
}