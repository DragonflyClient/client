package net.inceptioncloud.dragonfly.kernel.listener

import net.dragonfly.kernel.collector.ListenerSupplier

object ClientListenerSupplier : ListenerSupplier {

    override fun getPacketListeners() = listOf(
        OnlineAccountsListener::class, StartSessionResponseListener::class
    )

    override fun getServerListeners() = listOf(
        ConnectionListener::class
    )
}