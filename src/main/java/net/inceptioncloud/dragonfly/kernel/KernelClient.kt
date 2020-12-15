package net.inceptioncloud.dragonfly.kernel

import com.esotericsoftware.kryonet.Client
import net.dragonfly.kernel.collector.ListenerCollector.registerListeners
import net.dragonfly.kernel.collector.PacketCollector.registerPackets
import net.dragonfly.kernel.logger.SocketLogger
import net.dragonfly.kernel.packets.client.*
import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.kernel.listener.ClientListenerSupplier
import net.inceptioncloud.dragonfly.kernel.listener.ConnectionListener
import net.inceptioncloud.dragonfly.mc
import org.apache.logging.log4j.LogManager
import org.reflections.Reflections
import java.util.*
import kotlin.concurrent.fixedRateTimer

/**
 * The core class of the client that connects to the Dragonfly Kernel server
 */
object KernelClient {

    /**
     * The kryonet [Client] that establishes the connection between Dragonfly and the Kernel server
     */
    val client: Client

    /**
     * Contains the UUIDs of all Minecraft players that are currently using Dragonfly
     */
    var onlineAccounts: Array<String> = arrayOf()

    /**
     * The timer that is responsible for sending [KeepAlivePacket]s to the Kernel
     */
    private var keepAliveSender: Timer? = null

    /**
     * The timer that is responsible for sending [KeepActivePacket]s to the Kernel
     */
    private var keepActiveSender: Timer? = null

    /**
     * The timer that tries to reconnect to the Kernel server every 3 minutes
     */
    private var reconnectTimer: Timer? = null

    init {
        SocketLogger.setCustomLogger()

        client = Client().apply {
            start()
            registerPackets()
            registerListeners(ClientListenerSupplier)
        }
    }

    /**
     * Creates a connection between the Dragonfly client and the Kernel server. After the connection
     * has been built, the given [jwt] is used to authenticate with the server. If the [client] is
     * already [connected][Client.isConnected] this function will throw an error.
     */
    fun connect(jwt: String) {
        LogManager.getLogger().info("Connecting to Dragonfly Kernel server...")
        if (client.isConnected) error("Already connected to Dragonfly Kernel Server")

        try {
            with(client) {
                connect(500, "kernel.playdragonfly.net", 7331)
                sendTCP(StartSessionRequestPacket(jwt))
            }

            reconnectTimer?.cancel()
            reconnectTimer = null

            keepAliveSender = fixedRateTimer("Keep Alive Sender", true, 1000, 1000 * 60 * 2) {
                client.sendTCP(KeepAlivePacket())
            }

            keepActiveSender = fixedRateTimer("Keep Active Sender", true, 1000, 1000 * 20) {
                if (isActive) client.sendTCP(KeepActivePacket())
            }
        } catch (e: Throwable) {
            handleDisconnect()
            throw e
        }
    }

    /**
     * Closes the connection between the Dragonfly client and the Kernel server. If the client is
     * not [connected][Client.isConnected] this function will throw an error. Please note that
     * this function is not responsible for ending the [keepAliveSender] and [keepActiveSender] since
     * this is done by the [handleDisconnect] function which is called by the [ConnectionListener].
     */
    fun disconnect() {
        LogManager.getLogger().info("Disconnecting from Dragonfly Kernel server...")
        if (!client.isConnected) error("Not connected to Dragonfly Kernel server")

        client.close()
    }

    /**
     * This function takes all required actions after a connection has been closed. It should only
     * be called by the [ConnectionListener].
     */
    fun handleDisconnect() {
        keepAliveSender?.cancel()
        keepActiveSender?.cancel()

        keepAliveSender = null
        keepActiveSender = null

        LogManager.getLogger().info("Disconnected from Kernel Server")

        if (reconnectTimer == null) {
            reconnectTimer = fixedRateTimer("Reconnect Timer", true, 30_000, 1000 * 60 * 3) {
                try {
                    Dragonfly.account?.token?.let { connect(it) }
                } catch (e: Throwable) {
                    LogManager.getLogger().warn("Failed to reconnect to Kernel Server")
                    LogManager.getLogger().warn(e.toString())
                }
            }
        }
    }

    /**
     * Returns whether the client is currently connected to the server.
     */
    val isConnected: Boolean get() = client.isConnected

    /**
     * Returns whether the game is currently active and thus whether [KeepActivePacket]s should be sent.
     */
    val isActive: Boolean get() = mc.theWorld != null && System.currentTimeMillis() - mc.lastInteraction < 1000 * 60
}
