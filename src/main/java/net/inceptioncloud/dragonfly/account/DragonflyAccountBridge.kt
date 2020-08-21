package net.inceptioncloud.dragonfly.account

import com.google.gson.Gson
import net.inceptioncloud.dragonfly.Dragonfly
import java.io.File

object DragonflyAccountBridge {

    /**
     * The Dragonfly-internal file in which the saved accounts are stored.
     */
    private val tokenFile = File(Dragonfly.secretsDirectory, "token.txt")

    fun validateStoredToken(): DragonflyAccount? {
        if (!tokenFile.exists()) return null

        return validateToken(tokenFile.readText())
    }

    fun login(username: String, password: String): DragonflyAccount {
        val response = khttp.post(
            url = "https://api.playdragonfly.net/v1/authentication/login",
            json = mapOf(
                "name" to username,
                "password" to password
            )
        )

        if (response.statusCode != 200) throw Exception("Invalid response status code: ${response.statusCode}")
        if (!response.jsonObject.getBoolean("success")) throw Exception(response.jsonObject.getString("error"))

        return Gson().fromJson(response.text, DragonflyAccount::class.java)
    }

    private fun validateToken(token: String): DragonflyAccount {
        val response = khttp.post(
            url = "https://api.playdragonfly.net/v1/authentication/token",
            headers = mapOf(
                "Authorization" to "Bearer $token"
            )
        )

        if (response.statusCode != 200) throw Exception("Invalid response status code: ${response.statusCode}")
        if (!response.jsonObject.getBoolean("success")) throw Exception(response.jsonObject.getString("error"))

        return Gson().fromJson(response.text, DragonflyAccount::class.java)
    }
}