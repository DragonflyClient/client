package net.inceptioncloud.dragonfly.account

import com.google.gson.Gson
import khttp.responses.Response
import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.event.dragonfly.DragonflyAuthEvent
import net.inceptioncloud.dragonfly.event.dragonfly.DragonflyLoginEvent
import net.inceptioncloud.dragonfly.event.post
import net.inceptioncloud.dragonfly.overlay.modal.Modal
import net.inceptioncloud.dragonfly.utils.SecureFile

/**
 * A bridge between the Dragonfly client and the Dragonfly authentication servers
 * that performs the HTTP calls and stores/reads the token in/from the [tokenFile].
 */
object AuthenticationBridge {

    /**
     * The secure file in which the token is stored.
     */
    private val secureFile = SecureFile("bumfuzzle.png", "6),[\$^,8[MXa#!T\\t9&}txnsqdTPB\"5iF)[.ac)B")

    /**
     * Validates the token stored in the [secureFile]. Returns null if no token exists
     * and throws an exception if the token is invalid.
     */
    fun validateStoredToken() = readToken()?.let { validateToken(it) }

    /**
     * Reads the token that is stored in the [secureFile] or returns null if it
     * doesn't exist.
     */
    fun readToken(): String? = secureFile.read()

    /**
     * Stores the given [token] in the [secureFile].
     */
    fun storeToken(token: String) = secureFile.write(token)

    /**
     * Calls the Dragonfly authentication server to perform a login using the given
     * [username] and [password]. Returns the responded [DragonflyAccount] or throws an
     * exception if any error (like invalid credentials) occurred.
     */
    fun login(username: String, password: String, code: String? = null): DragonflyAccount {
        val response = khttp.post(
            url = "https://api.playdragonfly.net/v1/authentication/login",
            json = mapOf(
                "name" to username,
                "password" to password,
                "code" to code
            )
        )

        response.checkSuccess()

        val account = Gson().fromJson(response.text, DragonflyAccount::class.java)
        storeToken(account.token!!)

        DragonflyLoginEvent(account).post()
        DragonflyAuthEvent(account).post()

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
    fun showLoginModal(isAutomaticallyOpening: Boolean = false) {
        var otherFinished = false
        fun openModal() {
            if (otherFinished) {
                Modal.showModal(LoginModal(isAutomaticallyOpening))
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
    if (jsonObject.has("require2FA") && jsonObject.getBoolean("require2FA")) throw TwoFactorAuthException()
    if (!jsonObject.getBoolean("success")) throw Exception(jsonObject.getString("error"))
    if (statusCode != 200) throw Exception("Invalid response status code: $statusCode")
}
