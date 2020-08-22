package net.inceptioncloud.dragonfly.account

import com.google.gson.Gson
import khttp.responses.Response
import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.event.dragonfly.DragonflyLoginEvent
import net.inceptioncloud.dragonfly.event.post
import java.io.File

object DragonflyAccountBridge {

    /**
     * The Dragonfly-internal file in which the saved accounts are stored.
     */
    private val tokenFile = File(Dragonfly.secretsDirectory, "token.txt")

    fun validateStoredToken() = readStoredToken()?.let { validateToken(it) }

    fun readStoredToken() = tokenFile.takeIf { it.exists() }?.readText()

    fun login(username: String, password: String): DragonflyAccount {
        val response = khttp.post(
            url = "https://api.playdragonfly.net/v1/authentication/login",
            json = mapOf(
                "name" to username,
                "password" to password
            )
        )

        response.checkSuccess()

        val account = Gson().fromJson(response.text, DragonflyAccount::class.java)
        tokenFile.writeText(account.token!!)

        DragonflyLoginEvent(account).post()

        return account
    }

    private fun validateToken(token: String): DragonflyAccount {
        val response = khttp.post(
            url = "https://api.playdragonfly.net/v1/authentication/token",
            headers = mapOf(
                "Authorization" to "Bearer $token"
            )
        )

        response.checkSuccess()

        return Gson().fromJson(response.text, DragonflyAccount::class.java)
    }
}

fun Response.checkSuccess() {
    if (statusCode != 200) throw Exception("Invalid response status code: $statusCode")
    if (!jsonObject.getBoolean("success")) throw Exception(jsonObject.getString("error"))
}