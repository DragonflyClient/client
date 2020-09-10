package net.inceptioncloud.dragonfly.subscriber

import net.inceptioncloud.dragonfly.account.LoginSubscriber
import net.inceptioncloud.dragonfly.options.OptionSaveSubscriber
import net.inceptioncloud.dragonfly.design.zoom.ZoomSubscriber
import net.inceptioncloud.dragonfly.engine.inspector.InspectorSubscriber
import net.inceptioncloud.dragonfly.event.ModEventBus
import net.inceptioncloud.dragonfly.key.StartupGuiSubscriber
import net.inceptioncloud.dragonfly.mods.hotkeys.HotkeysController
import net.inceptioncloud.dragonfly.mods.keystrokes.KeystrokesSubscriber
import net.inceptioncloud.dragonfly.overlay.ScreenOverlay
import net.inceptioncloud.dragonfly.overlay.hotaction.HotAction
import net.inceptioncloud.dragonfly.overlay.modal.Modal
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
            register(ZoomSubscriber())
            register(StartupGuiSubscriber)
            register(DeveloperModeSubscriber)
            register(ShutdownSubscriber)
            register(ScreenOverlay)
            register(HotAction)
            register(Modal)
            register(InspectorSubscriber)
            register(KeystrokesSubscriber)
            register(LoginSubscriber)
            register(OptionSaveSubscriber)
            register(HotkeysController)
        }
    }
}