package net.inceptioncloud.dragonfly.apps.accountmanager

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.inceptioncloud.dragonfly.ui.taskbar.TaskbarApp
import net.minecraft.client.Minecraft
import org.apache.logging.log4j.LogManager
import java.io.File
import java.util.*

/**
 * ## Account Manager App
 *
 * Core of the Dragonfly account manager.
 */
object AccountManagerApp : TaskbarApp("Account Manager") {

    override fun open() = gui(AccountManagerUI(Minecraft.getMinecraft().currentScreen))

    /**
     * Reads the accounts that are stored in the `launcher_profiles.json` file and returns
     * a list of them mapped to [account objects][Account]. Returns null if an error occurred
     * or if the file doesn't exist.
     */
    fun readFromLauncher(): List<Account>? {
        return try {
            val file = File("launcher_profiles.json").takeIf { it.exists() } ?: return null
            val jsonObject = JsonParser().parse(file.reader()).asJsonObject
            val authenticationDatabase = jsonObject.get("authenticationDatabase").asJsonObject

            authenticationDatabase.entrySet()
                .map { authenticationDatabase.get(it.key).asJsonObject }
                .map {
                    val accessToken = it.get("accessToken").asString
                    val email = it.get("username").asString
                    val profiles = it.get("profiles").asJsonObject
                    val digits = profiles.entrySet().first().key
                    val uuid = parseWithoutDashes(digits)
                    val displayName = profiles.get(digits).asJsonObject.get("displayName").asString
                    val clientToken = jsonObject.get("clientToken").asString

                    Account(displayName, email, uuid, accessToken, clientToken)
                }
        } catch (e: Exception) {
            LogManager.getLogger().error("Could not read profiles from launcher!")
            e.printStackTrace()
            null
        }
    }

    /**
     * Create an [Account] by authenticating on the Mojang auth servers using an [email]
     * address and a [password]. If the authentication succeeded, an instance of [Account]
     * will be returned, otherwise null.
     */
    suspend fun authenticate(email: String, password: String): Account? = try {
        val payload = JsonObject().apply {
            val agent = JsonObject().apply {
                addProperty("name", "Minecraft")
                addProperty("version", 1)
            }
            add("agent", agent)

            addProperty("username", email)
            addProperty("password", password)
            addProperty("requestUser", true)
        }
        val response = request("authenticate", payload)
        val jsonObject = JsonParser().parse(response).asJsonObject

        if (!jsonObject.has("error")) {
            val user = jsonObject.get("user").asJsonObject
            val selectedProfile = jsonObject.get("selectedProfile").asJsonObject
            Account(
                selectedProfile.get("name").asString,
                user.get("username").asString,
                parseWithoutDashes(selectedProfile.get("id").asString),
                jsonObject.get("accessToken").asString,
                jsonObject.get("clientToken").asString
            )
        } else null
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    /**
     * Parses a [UUID] from the given [digits] that do not contain dashes for the UUID.
     */
    fun parseWithoutDashes(digits: String): UUID = UUID.fromString(
        digits.replace("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})".toRegex(), "$1-$2-$3-$4-$5")
    )
}