package net.inceptioncloud.dragonfly.account

import com.google.common.eventbus.Subscribe
import net.inceptioncloud.dragonfly.account.link.LinkBridge
import net.inceptioncloud.dragonfly.apps.accountmanager.Account
import net.inceptioncloud.dragonfly.apps.accountmanager.AccountManagerApp.parseWithoutDashes
import net.inceptioncloud.dragonfly.apps.accountmanager.AccountManagerApp.selectedAccount
import net.inceptioncloud.dragonfly.cosmetics.logic.CosmeticsManager
import net.inceptioncloud.dragonfly.engine.GraphicsEngine
import net.inceptioncloud.dragonfly.event.client.ClientStartupEvent
import net.inceptioncloud.dragonfly.event.dragonfly.DragonflyLoginEvent
import net.inceptioncloud.dragonfly.mc
import net.inceptioncloud.dragonfly.ui.screens.MainMenuUI
import net.minecraft.client.Minecraft
import org.apache.logging.log4j.LogManager

object LoginSubscriber {

    @Subscribe
    fun onClientStartup(event: ClientStartupEvent) {
        val session = Minecraft.getMinecraft().session
        if (session.token == null) return
        if (session.playerID == session.username) return

        val sessionAccount = selectedAccount ?: Account(session.username, "", parseWithoutDashes(session.playerID), session.token, "")
        LinkBridge.showModalForAccount(sessionAccount)
    }

    @Subscribe
    fun onDragonflyLogin(event: DragonflyLoginEvent) {
        GraphicsEngine.runAfter(200) {
            mc.addScheduledTask {
                ((mc.currentScreen as MainMenuUI).stage["login-status"] as LoginStatusWidget).runStructureUpdate()
            }
        }

        CosmeticsManager.dragonflyAccountCosmetics = CosmeticsManager.fetchDragonflyCosmetics(event.account.uuid)

        val session = Minecraft.getMinecraft().session
        if (session.token == null) return
        if (session.playerID == session.username) return

        val sessionAccount = selectedAccount ?: Account(session.username, "", parseWithoutDashes(session.playerID), session.token, "")
        LinkBridge.showModalForAccount(sessionAccount, event.account)
    }
}