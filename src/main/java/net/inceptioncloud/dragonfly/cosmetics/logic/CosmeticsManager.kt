package net.inceptioncloud.dragonfly.cosmetics.logic

import com.google.gson.JsonObject
import kotlinx.coroutines.*
import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.apps.accountmanager.Account
import net.inceptioncloud.dragonfly.cosmetics.types.wings.CosmeticWings
import net.inceptioncloud.dragonfly.cosmetics.Cosmetic
import net.inceptioncloud.dragonfly.mc
import net.inceptioncloud.dragonfly.utils.ListParameterizedType
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.entity.player.EntityPlayer
import okhttp3.Request
import org.apache.logging.log4j.LogManager
import java.util.*
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

    /**
     * A cache for already fetched cosmetic items saved per user. This cache can be cleared using
     * [clearCache].
     */
    private val cache = mutableMapOf<UUID, CosmeticDataList?>()

    init {
        LogManager.getLogger().info("Loading database models for cosmetics...")
        cosmetics = cosmetics.filter { cosmetic ->
            val fetched = getDatabaseModelById(cosmetic.cosmeticId)

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
                val typeToken = ListParameterizedType(JsonObject::class.java)
                return DatabaseModelList(Dragonfly.gson.fromJson(availableCosmetics, typeToken))
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return null
    }

    /**
     * Returns a database model in the form of a [JsonObject] for the given [cosmeticId].
     */
    fun getDatabaseModelById(cosmeticId: Int) = databaseModels?.firstOrNull { it.get("cosmeticId").asInt == cosmeticId }

    /**
     * Asynchronously loads the cosmetics by calling [fetchCosmetics] using the given [player].
     * Invokes the [callback] once the cosmetics were loaded.
     */
    @JvmStatic
    fun loadCosmetics(player: EntityPlayer, callback: Consumer<CosmeticDataList?>) {
        val uuid = player.gameProfile.id ?: return
        if (cache.containsKey(uuid)) return callback.accept(cache[uuid])

        GlobalScope.launch(Dispatchers.IO) {
            val cosmetics = fetchCosmetics(uuid)
            cache[uuid] = cosmetics
            callback.accept(cosmetics)
        }
    }

    /**
     * Clears the [cache] for the already fetched cosmetic items. If a [uuid] is specified,
     * only the cached cosmetics for this [uuid] are removed. Otherwise, the whole cache is cleared.
     */
    fun clearCache(uuid: UUID? = null) {
        LogManager.getLogger().info("Clearing cosmetics cache for ${uuid ?: "all players"}")
        if (uuid != null) cache.remove(uuid) else cache.clear()
    }

    /**
     * Refreshes the cosmetics for the targets. If a [uuid] is specified, the target is only
     * the entity with this UUID. Otherwise, the whole [cache] will be cleared and the cosmetics
     * for all entities in the cache are [reloaded][AbstractClientPlayer.loadCosmetics].
     */
    @JvmStatic
    @JvmOverloads
    fun refreshCosmetics(uuid: UUID? = null) {
        GlobalScope.launch {
            databaseModels = loadDatabaseModels()
            mc.addScheduledTask {
                val targetEntities = if (uuid != null) {
                    mc.theWorld.getEntities(EntityPlayer::class.java) { it?.gameProfile?.id == uuid }
                } else {
                    mc.theWorld.getEntities(EntityPlayer::class.java) { it?.gameProfile?.id in cache.keys }
                }
                clearCache(uuid)
                targetEntities
                    ?.mapNotNull { it as? AbstractClientPlayer }
                    ?.forEach { it.loadCosmetics() } // reload cosmetics for targets
            }
        }
    }

    /**
     * Fetches the cosmetics of the given [uuid] from the Dragonfly servers. This function
     * will return a list of all [cosmetics][CosmeticData]. If an error occurred during the
     * request (missing internet connection, account not linked) this function will return
     * null.
     */
    fun fetchCosmetics(uuid: UUID): CosmeticDataList? {
        try {
            val request = Request.Builder()
                .url("https://api.playdragonfly.net/v1/cosmetics/find?minecraft=$uuid")
                .build()
            val response = Dragonfly.httpClient.newCall(request).execute()
                .use { response -> response.body!!.string() }
                .let { Dragonfly.gson.fromJson(it, JsonObject::class.java).asJsonObject }

            if (response.get("success").asBoolean) {
                val cosmetics = response.getAsJsonArray("cosmetics")
                val typeToken = ListParameterizedType(CosmeticData::class.java)
                return CosmeticDataList(Dragonfly.gson.fromJson(cosmetics, typeToken))
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return null
    }

    /**
     * Fetches the cosmetics associated with the currently logged in Dragonfly account.
     */
    fun fetchDragonflyCosmetics(dragonflyUUID: String? = Dragonfly.account?.uuid): CosmeticDataList? {
        try {
            val request = Request.Builder()
                .url("https://api.playdragonfly.net/v1/cosmetics/find?dragonfly=${dragonflyUUID ?: return null}")
                .build()
            val response = Dragonfly.httpClient.newCall(request).execute()
                .use { response -> response.body!!.string() }
                .let { Dragonfly.gson.fromJson(it, JsonObject::class.java).asJsonObject }

            if (response.get("success").asBoolean) {
                val cosmetics = response.getAsJsonArray("cosmetics")
                val typeToken = ListParameterizedType(CosmeticData::class.java)
                return CosmeticDataList(Dragonfly.gson.fromJson(cosmetics, typeToken))
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return null
    }
}