package net.inceptioncloud.dragonfly.kernel.listener

import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import net.inceptioncloud.dragonfly.kernel.KernelClient

/**
 * A server listener that calls the [KernelClient.handleDisconnect] function once the connection
 * to the Kernel server is closed.
 */
class ConnectionListener : Listener() {

    override fun disconnected(connection: Connection) {
        KernelClient.handleDisconnect()
    }
}
