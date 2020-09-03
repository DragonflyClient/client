package net.inceptioncloud.dragonfly.apps.accountmanager

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.inceptioncloud.dragonfly.apps.accountmanager.AccountManagerApp.parseWithoutDashes
import net.inceptioncloud.dragonfly.options.OptionKey
import net.minecraft.util.Session
import java.awt.image.BufferedImage
import java.net.Proxy
import java.net.URL
import java.util.*
import javax.imageio.ImageIO

/**
 * Represents a user account that can be safely read and stored in the JSON format. It does
 * not contain the password of the account but uses access- and client-tokens to authenticate
 * on the Mojang auth servers. Additional account properties are also given.
 *
 * @param displayName the display name (username) of the account
 * @param email the email address that was used to authenticate on the Mojang auth servers
 * @param uuid the [UUID] of the account
 * @param accessToken the access token that is stored in the client session in order to
 * authenticate with online servers (uses the JWT format)
 * @param clientToken the client token that the Mojang auth servers use to identify clients
 * that generate access tokens (either from the launcher or randomly generated by the server)
 */
data class Account(
    var displayName: String,
    var email: String,
    var uuid: UUID,
    var accessToken: String,
    var clientToken: String
) {

    /**
     * Cache for the player skull texture downloaded from the craftatar api.
     */
    @Transient
    var skull: BufferedImage? = null

    /**
     * Validates the account (strictly speaking the [accessToken] together with the [clientToken])
     * and returns whether the validation was successful.
     */
    suspend fun validate(): Boolean = try {
        val payload = JsonObject().apply {
            addProperty("accessToken", accessToken)
            addProperty("clientToken", clientToken)
        }
        request("validate", payload).isEmpty()
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    /**
     * Invalidates the [accessToken]. Returns whether the invalidation was a success.
     */
    suspend fun invalidate(): Boolean = try {
        val payload = JsonObject().apply {
            addProperty("accessToken", accessToken)
            addProperty("clientToken", clientToken)
        }
        request("invalidate", payload).isEmpty()
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    /**
     * Refreshes the account properties (especially the [accessToken] if it was invalidated
     * by a login with another client token). Returns whether the refresh was a success.
     */
    suspend fun refresh(): Boolean = try {
        val payload = JsonObject().apply {
            addProperty("accessToken", accessToken)
            addProperty("clientToken", clientToken)
            addProperty("requestUser", true)
        }
        val response = request("refresh", payload)
        val jsonObject = JsonParser().parse(response).asJsonObject

        if (!jsonObject.has("error")) {
            val selectedProfile = jsonObject.get("selectedProfile").asJsonObject
            val user = jsonObject.get("user").asJsonObject

            this.accessToken = jsonObject.get("accessToken").asString
            this.clientToken = jsonObject.get("clientToken").asString
            this.displayName = selectedProfile.get("name").asString
            this.email = user.get("username").asString
            this.uuid = parseWithoutDashes(selectedProfile.get("id").asString)
            true
        } else false
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    /**
     * Validates the account trying to [refresh] it if needed.
     */
    suspend fun validateWithRefresh(): Boolean = validate() || refresh()

    /**
     * Downloads the player skull texture using the craftatar api or returns the [skull]
     * if it isn't null.
     */
    suspend fun retrieveSkull(): BufferedImage? {
        return withContext(Dispatchers.IO) {
            if (skull != null)
                return@withContext skull!!

            val downloaded = kotlin.runCatching {
                ImageIO.read(URL("https://crafatar.com/avatars/$uuid?size=100&default=MHF_Steve&overlay=true"))
            }.getOrNull()

            skull = downloaded
            return@withContext downloaded
        }
    }

    /**
     * Creates a [Session] based on the account data.
     */
    fun toSession() = Session(displayName, uuid.toSimpleString(), accessToken, "mojang")

    /**
     * Returns an option key that holds whether the user wants to skip the linking process.
     */
    fun getSkipLinkOption(): OptionKey<Boolean> = OptionKey.newInstance(Boolean::class.java)
        .key("skipLink:$uuid")
        .defaultValue(false)
        .validator { true }
        .build()
}

/**
 * The Yggdrasil authentication service that is targeted by all outgoing requests to the Mojang
 * authentication servers.
 */
val authenticationService = YggdrasilAuthenticationService(Proxy.NO_PROXY, "")

/**
 * Convenient function for sending a request to the specified [route] of the Mojang authentication
 * servers with the given [payload] in the body.
 */
@Suppress("BlockingMethodInNonBlockingContext")
suspend fun request(route: String, payload: JsonObject): String = withContext(Dispatchers.IO) {
    authenticationService.performPostRequest(
        URL("https://authserver.mojang.com/$route"), payload.toString(), "application/json"
    )
}