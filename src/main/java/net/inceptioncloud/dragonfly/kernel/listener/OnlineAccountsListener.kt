package net.inceptioncloud.dragonfly.kernel.listener

import com.esotericsoftware.kryonet.Connection
import net.dragonfly.kernel.packets.server.OnlineAccountsPacket
import net.inceptioncloud.dragonfly.kernel.KernelClient
import net.inceptioncloud.dragonfly.utils.Keep

/***
 * Listens to the [OnlineAccountsPacket] and updates the [KernelClient.onlineAccounts] property
 * so the ui can reflect the changes.
 */
@Keep
class OnlineAccountsListener {

    fun onSessionStartResponse(connection: Connection, packet: OnlineAccountsPacket) {
        KernelClient.onlineAccounts = packet.onlineAccounts ?: arrayOf()
    }
}
