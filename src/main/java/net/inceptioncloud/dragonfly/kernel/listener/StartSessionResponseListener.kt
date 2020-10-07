package net.inceptioncloud.dragonfly.kernel.listener

import com.esotericsoftware.kryonet.Connection
import net.dragonfly.kernel.packets.client.UpdateMinecraftAccountPacket
import net.dragonfly.kernel.packets.server.StartSessionResponsePacket
import net.inceptioncloud.dragonfly.kernel.KernelClient
import net.inceptioncloud.dragonfly.mc
import net.inceptioncloud.dragonfly.utils.Keep
import org.apache.logging.log4j.LogManager

/***
 * Listens to the [StartSessionResponsePacket] and reacts based on whether the session start
 * was successful. Disconnects from the server when it wasn't successful.
 */
@Keep
class StartSessionResponseListener {

    fun onSessionStartResponse(connection: Connection, packet: StartSessionResponsePacket) {
        if (packet.success == true) {
            LogManager.getLogger().info("Successfully authenticated on Dragonfly Kernel server");
            connection.sendTCP(UpdateMinecraftAccountPacket(mc.session?.profile?.id?.toString()))
        } else {
            LogManager.getLogger().info("Could not authenticate on Dragonfly Kernel server! Disconnecting...");
            KernelClient.disconnect()
        }
    }
}
