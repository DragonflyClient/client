package net.inceptioncloud.minecraftmod.subscriber

import net.inceptioncloud.minecraftmod.event.ModEventBus
import net.inceptioncloud.minecraftmod.key.StartupGuiSubscriber
import net.inceptioncloud.minecraftmod.tracking.transitions.FileSaveSubscriber
import net.inceptioncloud.minecraftmod.tracking.transitions.TickSubscriber

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
            register(DeveloperModeSubscriber())
        }
    }
}