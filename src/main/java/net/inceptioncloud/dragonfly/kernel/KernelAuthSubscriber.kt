package net.inceptioncloud.dragonfly.kernel

import com.google.common.eventbus.Subscribe
import net.inceptioncloud.dragonfly.event.dragonfly.DragonflyAuthEvent

object KernelAuthSubscriber {

    @Subscribe
    fun onAuth(event: DragonflyAuthEvent) {
        if (KernelClient.isConnected) KernelClient.disconnect()
        KernelClient.connect(event.account.token!!)
    }
}
