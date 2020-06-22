package net.inceptioncloud.minecraftmod.key

import com.google.common.eventbus.Subscribe
import net.inceptioncloud.minecraftmod.event.gui.StartupGuiEvent
import net.inceptioncloud.minecraftmod.key.ui.AttachingKeyUI

/**
 * Listens to the [StartupGuiEvent] and changes the target gui to the [AttachingKeyUI].
 */
object StartupGuiSubscriber {
    @Subscribe
    fun onStartupGui(event: StartupGuiEvent) {
        event.target = AttachingKeyUI("L9AJOT-XI25G0F9-QWJB3W5K-94JQD1")
    }
}