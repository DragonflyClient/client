package net.inceptioncloud.dragonfly.account

import com.google.gson.Gson
import khttp.responses.Response
import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.account.AuthenticationBridge.tokenFile
import net.inceptioncloud.dragonfly.event.dragonfly.DragonflyAuthEvent
import net.inceptioncloud.dragonfly.event.dragonfly.DragonflyLoginEvent
import net.inceptioncloud.dragonfly.event.post
import net.inceptioncloud.dragonfly.overlay.modal.Modal
import org.apache.logging.log4j.LogManager
import java.io.File
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random


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
     * The secret key that is used for encryption of the Dragonfly token.
     */
    private var secretKey = prepareSecreteKey()

    /**
     * Validates the token stored in the [tokenFile]. Returns null if no token exists
     * and throws an exception if the token is invalid.
     */
    fun validateStoredToken() = readToken()?.let { validateToken(it) }

    /**
     * Reads the token that is stored in the [tokenFile] or returns null if it
     * doesn't exist.
     */
    fun readToken(): String? = kotlin.runCatching {
        if (!tokenFile.exists()) return null
        val content = tokenFile.readText().replace("\n", "")

        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)

        return String(cipher.doFinal(Base64.getDecoder().decode(content)))
    }.getOrNull()

    /**
     * Stores the given [token] in the [tokenFile].
     */
    fun storeToken(token: String) {
        try {
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)

            val content = Base64.getEncoder().encodeToString(cipher.doFinal(token.toByteArray()))
            tokenFile.writeText(content.toCharArray().joinToString("") {
                if (Random.nextBoolean() && Random.nextBoolean() && Random.nextBoolean()) "$it\n" else "$it"
            })
        } catch (e: Throwable) {
            LogManager.getLogger().error("Failed to store token!")
            e.printStackTrace()
        }
    }

    /**
     * Prepares the secret key that is used for encryption of the Dragonfly token.
     */
    private fun prepareSecreteKey(): SecretKeySpec {
        var key = "6),[\$^,8[MXa#!T\\t9&}txnsqdTPB\"5iF)[.ac)B".toByteArray()
        val sha = MessageDigest.getInstance("SHA-1")
        key = sha.digest(key)
        key = key.copyOf(newSize = 16)
        return SecretKeySpec(key, "AES")
    }

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
