package net.inceptioncloud.dragonfly.account.link

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.account.checkSuccess
import net.inceptioncloud.dragonfly.apps.accountmanager.Account
import net.inceptioncloud.dragonfly.overlay.modal.Modal
import java.lang.IllegalStateException
import java.util.*

object LinkBridge {

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

    fun showModalForAccount(account: Account) {
        if (account.getSkipLinkOption().get()) return
        Modal.showModal(LinkModal(account))
    }
}