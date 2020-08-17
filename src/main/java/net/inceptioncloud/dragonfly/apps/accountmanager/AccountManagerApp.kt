package net.inceptioncloud.dragonfly.apps.accountmanager

import com.google.gson.*
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
     * The Dragonfly-internal file in which the saved accounts are stored.
     */
    private val accountsFile = File("dragonfly/accounts.json")

    /**
     * A mutable list of accounts that have been stored in the launcher or Dragonfly-internal
     * account storages. This list can be modified to reflect changes in the account manager.
     * Its content is stored when [storeAccounts] is called.
     */
    val accounts: MutableList<Account> = kotlin.run {
        val fromLauncher = readFromLauncher() ?: listOf()
        val fromFile = readFromAccountsFile() ?: listOf()

        (fromLauncher + fromFile).distinctBy { it.uuid }.toMutableList()
    }

    init {
        storeAccounts()
    }

    /**
     * Stores the [accounts] in the accounts.json file catching any errors.
     */
    fun storeAccounts() {
        try {
            accountsFile.writeText(GsonBuilder().setPrettyPrinting().create().toJson(accounts))
        } catch (e: Exception) {
            LogManager.getLogger().error("Could not store accounts!")
            e.printStackTrace()
        }
    }

    /**
     * Reads the accounts that are stored in the `accounts.json` file and returns a list of
     * them or null if an error occurred or if the file doesn't exist.
     */
    private fun readFromAccountsFile(): List<Account>? {
        return try {
            val file = accountsFile.takeIf { it.exists() } ?: return null

            Gson().fromJson(file.reader(), AccountList::class.java)
        } catch (e: Exception) {
            LogManager.getLogger().error("Could not read accounts from accounts.json!")
            e.printStackTrace()
            null
        }
    }

    /**
     * Reads the accounts that are stored in the `launcher_profiles.json` file and returns
     * a list of them mapped to [account objects][Account]. Returns null if an error occurred
     * or if the file doesn't exist.
     */
    private fun readFromLauncher(): List<Account>? {
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