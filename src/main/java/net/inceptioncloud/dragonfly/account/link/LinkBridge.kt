package net.inceptioncloud.dragonfly.account.link

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.account.DragonflyAccount
import net.inceptioncloud.dragonfly.account.checkSuccess
import net.inceptioncloud.dragonfly.apps.accountmanager.Account
import net.inceptioncloud.dragonfly.overlay.modal.Modal
import org.apache.logging.log4j.LogManager
import java.lang.IllegalStateException
import java.util.*

/**
 * A bridge between the Dragonfly client and the Dragonfly backend that performs
 * the HTTP calls to link Minecraft accounts to the user's Dragonfly account.
 */
object LinkBridge {

    /**
     * Performs the HTTP call to link the Minecraft account with the given [uuid]
     * and [accessToken] to the currently authenticated [Dragonfly account][Dragonfly.account].
     */
    fun link(uuid: UUID, accessToken: String) {
        if (Dragonfly.account == null) throw IllegalStateException("Must be authenticated with Dragonfly")

        val response = khttp.post(
            url = "https://api.playdragonfly.net/v1/minecraft/link",
            headers = mapOf(
                "Authorization" to "Bearer ${Dragonfly.account!!.token}"
            ),
            data = accessToken
        )

        response.checkSuccess()

        val newLinkedAccounts = Dragonfly.account!!.linkedMinecraftAccounts?.toMutableList() ?: mutableListOf()
        newLinkedAccounts.add(uuid.toString())
        Dragonfly.account!!.linkedMinecraftAccounts = newLinkedAccounts
    }

    /**
     * Opens the [LinkModal] to prompt the user whether he wants to link the Minecraft [account]
     * to the given [Dragonfly account][dragonflyAccount]. Note that in some cases this method is
     * called before the [Dragonfly.account] value has been changed and thus the Dragonfly account
     * is passed as the [dragonflyAccount] parameter.
     */
    fun showModalForAccount(account: Account, dragonflyAccount: DragonflyAccount? = Dragonfly.account) {
        if (dragonflyAccount == null) return
        if (dragonflyAccount.linkedMinecraftAccounts?.contains(account.uuid.toString()) == true) return
        if (account.getSkipLinkOption().get()) return

        Modal.showModal(LinkModal(account))
    }
}