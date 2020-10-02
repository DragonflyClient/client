package net.inceptioncloud.dragonfly.kernel

import com.esotericsoftware.kryonet.Client
import net.dragonfly.kernel.collector.ListenerCollector.registerListeners
import net.dragonfly.kernel.collector.PacketCollector.registerPackets
import net.dragonfly.kernel.logger.SocketLogger
import net.dragonfly.kernel.packets.client.*
import net.inceptioncloud.dragonfly.mc
import java.util.*
import kotlin.concurrent.fixedRateTimer

object KernelClient {

    private var client: Client

    private var keepAliveSender: Timer? = null
    private var keepActiveSender: Timer? = null

    init {
        SocketLogger.setCustomLogger()

        client = Client().apply {
            start()
            registerPackets()
            registerListeners("net.inceptioncloud.dragonfly.kernel.listener")
        }
    }

    fun connect(jwt: String) {
        if (client.isConnected) error("Already connected to Dragonfly Kernel Server")

        with(client) {
            connect(500, "kernel.playdragonfly.net", 7331)
            sendTCP(StartSessionRequestPacket(jwt))
        }

        keepAliveSender = fixedRateTimer("Keep Alive Sender", false, 1000, 1000 * 60 * 2) {
            client.sendTCP(KeepAlivePacket())
        }

        keepActiveSender = fixedRateTimer("Keep Active Sender", false, 1000, 1000 * 20) {
            if (isActive) client.sendTCP(KeepActivePacket())
        }
    }

    fun disconnect() {
        if (!client.isConnected) error("Not connected to Dragonfly Kernel server")

        client.close()
        keepAliveSender?.cancel()
        keepActiveSender?.cancel()

        keepAliveSender = null
        keepActiveSender = null
    }

    val isConnected: Boolean get() = client.isConnected

    val isActive: Boolean get() = mc.theWorld != null && System.currentTimeMillis() - mc.lastInteraction < 1000 * 60
}
