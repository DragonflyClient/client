package net.inceptioncloud.dragonfly.kernel.listener

import com.esotericsoftware.kryonet.Connection
import net.dragonfly.kernel.collector.PacketListener
import net.dragonfly.kernel.packets.server.OnlineAccountsPacket
import net.dragonfly.kernel.packets.server.StartSessionResponsePacket
import net.inceptioncloud.dragonfly.kernel.KernelClient
import org.apache.logging.log4j.LogManager

/***
 * Listens to the [OnlineAccountsPacket] and updates the [KernelClient.onlineAccounts] property
 * so the ui can reflect the changes.
 */
@PacketListener
class OnlineAccountsListener {

    @PacketListener
    fun onSessionStartResponse(connection: Connection, packet: OnlineAccountsPacket) {
        KernelClient.onlineAccounts = packet.onlineAccounts ?: arrayOf()
    }
}
