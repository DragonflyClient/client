package net.inceptioncloud.dragonfly.kernel.subscriber

import com.google.common.eventbus.Subscribe
import net.inceptioncloud.dragonfly.event.dragonfly.DragonflyAuthEvent
import net.inceptioncloud.dragonfly.kernel.KernelClient

/**
 * Listens to the [DragonflyAuthEvent] to start a connection to the Dragonfly Kernel server.
 */
object KernelAuthSubscriber {

    @Subscribe
    fun onAuth(event: DragonflyAuthEvent) {
        if (KernelClient.isConnected) KernelClient.disconnect()
        KernelClient.connect(event.account.token!!)
    }
}
