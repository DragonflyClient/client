package net.inceptioncloud.dragonfly.account.link

import com.google.common.eventbus.Subscribe
import net.inceptioncloud.dragonfly.Dragonfly.account
import net.inceptioncloud.dragonfly.apps.accountmanager.Account
import net.inceptioncloud.dragonfly.apps.accountmanager.AccountManagerApp.parseWithoutDashes
import net.inceptioncloud.dragonfly.apps.accountmanager.AccountManagerApp.selectedAccount
import net.inceptioncloud.dragonfly.event.client.ClientStartupEvent
import net.inceptioncloud.dragonfly.event.dragonfly.DragonflyLoginEvent
import net.minecraft.client.Minecraft

object LinkSubscriber {

    @Subscribe
    fun onClientStartup(event: ClientStartupEvent) {
        val session = Minecraft.getMinecraft().session
        if (session.token == null) return

        val sessionAccount = selectedAccount ?: Account(session.username, "", parseWithoutDashes(session.playerID), session.token, "")
        if (account != null && account?.linkedMinecraftAccounts?.contains(sessionAccount.uuid.toString()) != true) {
            LinkBridge.showModalForAccount(sessionAccount)
        }
    }

    @Subscribe
    fun onDragonflyLogin(event: DragonflyLoginEvent) {
        val session = Minecraft.getMinecraft().session
        if (session.token == null) return

        val sessionAccount = selectedAccount ?: Account(session.username, "", parseWithoutDashes(session.playerID), session.token, "")
        if (event.account.linkedMinecraftAccounts?.contains(sessionAccount.uuid.toString()) != true) {
            LinkBridge.showModalForAccount(sessionAccount)
        }
    }
}