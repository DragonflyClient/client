package net.inceptioncloud.dragonfly.cosmetics.logic

import com.google.gson.JsonObject
import kotlinx.coroutines.*
import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.cosmetics.types.wings.CosmeticWings
import net.inceptioncloud.dragonfly.cosmetics.Cosmetic
import net.minecraft.entity.player.EntityPlayer
import okhttp3.Request
import org.apache.logging.log4j.LogManager
import java.util.function.Consumer

/**
 * Handles the communication between the client and the server for loading
 * Dragonfly cosmetics from the database.
 */
object CosmeticsManager {

    /**
     * The list of all available cosmetics. When a new cosmetic item is created, the developer has
     * to add it to this list in order to make it available.
     */
    @JvmStatic
    var cosmetics = listOf(CosmeticWings)

    /**
     * Contains the database models provided by [loadDatabaseModels]. This variable is computed when
     * the Dragonfly client starts.
     */
    var databaseModels = loadDatabaseModels()

    init {
        LogManager.getLogger().info("Loading database models for cosmetics...")
        cosmetics = cosmetics.filter { cosmetic ->
            val fetched = databaseModels?.firstOrNull { model -> model.get("cosmeticId").asInt == cosmetic.cosmeticId }

            if (fetched != null) {
                cosmetic.databaseModel = fetched
                LogManager.getLogger().info("Database model for cosmetic ${cosmetic::class.simpleName} is $fetched")
                true
            } else {
                LogManager.getLogger().info("Unable to find database model for cosmetic ${cosmetic::class.simpleName}! Skipping...")
                false
            }
        }
    }

    /**
     * Loads the available cosmetics from the database using the Dragonfly servers. All [cosmetics]
     * that don't have an associated database model (specified by their [Cosmetic.cosmeticId]) will
     * be removed as soon as the [CosmeticsManager] is initialized.
     */
    private fun loadDatabaseModels(): DatabaseModelList? {
        try {
            val request = Request.Builder()
                .url("https://api.playdragonfly.net/v1/cosmetics/available")
                .build()
            val response = Dragonfly.httpClient.newCall(request).execute()
                .use { response -> response.body!!.string() }
                .let { Dragonfly.gson.fromJson(it, JsonObject::class.java).asJsonObject }

            if (response.get("success").asBoolean) {
                val availableCosmetics = response.getAsJsonArray("availableCosmetics")
                return Dragonfly.gson.fromJson(availableCosmetics, DatabaseModelList::class.java)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return null
    }

    /**
     * Asynchronously loads the cosmetics by calling [fetchCosmetics] using the given [player].
     * Invokes the [callback] once the cosmetics were loaded.
     */
    @JvmStatic
    fun loadCosmetics(player: EntityPlayer, callback: Consumer<CosmeticDataList?>) {
        if (player.gameProfile.id == null) return

        LogManager.getLogger().info("Loading cosmetics for ${player.gameProfile.name}...")
        GlobalScope.launch(Dispatchers.IO) {
            val cosmetics = fetchCosmetics(player)
            callback.accept(cosmetics)
            LogManager.getLogger().info("Cosmetics for ${player.gameProfile.name} loaded: $cosmetics")
        }
    }

    /**
     * Fetches the cosmetics of the given [player] from the Dragonfly servers. This function
     * will return a list of all [cosmetics][CosmeticData]. If an error occurred during the
     * request (missing internet connection, account not linked) this function will return
     * null.
     */
    private fun fetchCosmetics(player: EntityPlayer): CosmeticDataList? {
        val id = player.gameProfile.id

        try {
            val request = Request.Builder()
                .url("https://api.playdragonfly.net/v1/cosmetics/find?uuid=$id")
                .build()
            val response = Dragonfly.httpClient.newCall(request).execute()
                .use { response -> response.body!!.string() }
                .let { Dragonfly.gson.fromJson(it, JsonObject::class.java).asJsonObject }

            if (response.get("success").asBoolean) {
                val cosmetics = response.getAsJsonArray("cosmetics")
                return Dragonfly.gson.fromJson(cosmetics, CosmeticDataList::class.java)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return null
    }
}