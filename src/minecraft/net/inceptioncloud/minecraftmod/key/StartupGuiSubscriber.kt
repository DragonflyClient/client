package net.inceptioncloud.minecraftmod.key

import com.google.common.eventbus.Subscribe
import net.inceptioncloud.minecraftmod.event.gui.StartupGuiEvent
import net.inceptioncloud.minecraftmod.ui.screens.RedeemKeyUI

/**
 * Listens to the [StartupGuiEvent] and changes the target gui to the [RedeemKeyUI].
 */
object StartupGuiSubscriber {
    @Subscribe
    fun onStartupGui(event: StartupGuiEvent) {
        event.target = RedeemKeyUI
    }
}