package net.inceptioncloud.dragonfly.kernel.listener

import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import net.dragonfly.kernel.collector.ServerListener
import net.inceptioncloud.dragonfly.kernel.KernelClient

@ServerListener
class ConnectionListener : Listener() {
    override fun disconnected(connection: Connection) {
        KernelClient.handleDisconnect() // <3
    }
}
