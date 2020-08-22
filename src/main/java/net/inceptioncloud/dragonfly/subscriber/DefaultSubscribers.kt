package net.inceptioncloud.dragonfly.subscriber

import net.inceptioncloud.dragonfly.engine.inspector.InspectorSubscriber
import net.inceptioncloud.dragonfly.event.ModEventBus
import net.inceptioncloud.dragonfly.key.StartupGuiSubscriber
import net.inceptioncloud.dragonfly.keystrokes.KeyStrokesSubscriber
import net.inceptioncloud.dragonfly.overlay.ScreenOverlay
import net.inceptioncloud.dragonfly.overlay.hotaction.HotAction
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
            register(FileSaveSubscriber())
            register(TickSubscriber())
            register(LastServerSaveSubscriber())
            register(StartupGuiSubscriber)
            register(DeveloperModeSubscriber)
            register(ShutdownSubscriber)
            register(ScreenOverlay)
            register(HotAction)
            register(InspectorSubscriber)
            register(KeyStrokesSubscriber)
        }
    }
}