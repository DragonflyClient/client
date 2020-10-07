package net.inceptioncloud.dragonfly.kernel.listener

import com.esotericsoftware.kryonet.Connection
import net.dragonfly.kernel.packets.server.StartSessionResponsePacket
import net.inceptioncloud.dragonfly.kernel.KernelClient
import org.apache.logging.log4j.LogManager

/***
 * Listens to the [StartSessionResponsePacket] and reacts based on whether the session start
 * was successful. Disconnects from the server when it wasn't successful.
 */
class StartSessionResponseListener {

    fun onSessionStartResponse(connection: Connection, packet: StartSessionResponsePacket) {
        if (packet.success == true) {
            LogManager.getLogger().info("Successfully authenticated on Dragonfly Kernel server");
        } else {
            LogManager.getLogger().info("Could not authenticate on Dragonfly Kernel server! Disconnecting...");
            KernelClient.disconnect()
        }
    }
}
