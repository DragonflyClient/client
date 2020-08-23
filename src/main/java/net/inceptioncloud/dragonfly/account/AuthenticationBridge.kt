package net.inceptioncloud.dragonfly.account

import com.google.gson.Gson
import khttp.responses.Response
import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.event.dragonfly.DragonflyLoginEvent
import net.inceptioncloud.dragonfly.event.post
import net.inceptioncloud.dragonfly.overlay.modal.Modal
import java.io.File

/**
 * A bridge between the Dragonfly client and the Dragonfly authentication servers
 * that performs the HTTP calls and stores/reads the token in/from the [tokenFile].
 */
object AuthenticationBridge {

    /**
     * The Dragonfly-internal file in which the saved accounts are stored.
     */
    private val tokenFile = File(Dragonfly.secretsDirectory, "token.txt")

    /**
     * Validates the token stored in the [tokenFile]. Returns null if no token exists
     * and throws an exception if the token is invalid.
     */
    fun validateStoredToken() = readStoredToken()?.let { validateToken(it) }

    /**
     * Reads the token that is stored in the [tokenFile] or returns null if it
     * doesn't exist.
     */
    fun readStoredToken() = tokenFile.takeIf { it.exists() }?.readText()

    /**
     * Calls the Dragonfly authentication server to perform a login using the given
     * [username] and [password]. Returns the responded [DragonflyAccount] or throws an
     * exception if any error (like invalid credentials) occurred.
     */
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

    /**
     * Validates the given [token] by calling the Dragonfly authentication server. Returns the
     * responded [DragonflyAccount] without the [token property][DragonflyAccount.token] set or
     * throws an exception if any error (like expiration of the token) occurred.
     */
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

    /**
     * Preloads the required font renderers before opening the [LoginModal] via [Modal.showModal]
     * to prevent lags during the animation.
     */
    fun showLoginModal() {
        var otherFinished = false
        fun openModal() {
            if (otherFinished) {
                Modal.showModal(LoginModal())
            }
        }
        Dragonfly.fontManager.defaultFont.fontRendererAsync(size = 60, useScale = false) {
            openModal()
            otherFinished = true
        }
        Dragonfly.fontManager.defaultFont.fontRendererAsync(size = 50, useScale = false) {
            openModal()
            otherFinished = true
        }
    }
}

/**
 * Convenient function that checks if a request was successful by validating
 * the response using the common Dragonfly backend response schema. Throws an
 * exception with a detailed message if the request wasn't successful.
 */
fun Response.checkSuccess() {
    if (statusCode != 200) throw Exception("Invalid response status code: $statusCode")
    if (!jsonObject.getBoolean("success")) throw Exception(jsonObject.getString("error"))
}