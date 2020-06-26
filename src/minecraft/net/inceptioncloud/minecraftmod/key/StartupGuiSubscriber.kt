package net.inceptioncloud.minecraftmod.key

import com.google.common.eventbus.Subscribe
import net.inceptioncloud.minecraftmod.event.gui.StartupGuiEvent
import net.inceptioncloud.minecraftmod.key.ui.AttachingKeyUI
import net.inceptioncloud.minecraftmod.key.ui.EnterKeyUI

/**
 * Listens to the [StartupGuiEvent] and changes the target gui to the [AttachingKeyUI].
 */
object StartupGuiSubscriber {
    @Subscribe
    fun onStartupGui(event: StartupGuiEvent) {
        event.target = EnterKeyUI()
    }
}