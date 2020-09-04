package net.inceptioncloud.dragonfly.cosmetics.logic

import com.google.gson.JsonObject
import kotlinx.coroutines.*
import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.cosmetics.types.wings.CosmeticWings
import net.inceptioncloud.dragonfly.cosmetics.Cosmetic
import net.inceptioncloud.dragonfly.mc
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
                return Dragonfly.gson.fromJson(availableCosmetics, DatabaseModelList::class.java)
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
            val cosmetics = fetchCosmetics(player)
            cache[uuid] = cosmetics
            callback.accept(cosmetics)
        }
    }

    /**
     * Clears the [cache] for the already fetched cosmetic items. If a [uuid] is specified,
     * only the cached cosmetics for this [uuid] are removed. Otherwise, the whole cache is cleared.
     */
    fun clearCache(uuid: UUID? = null) {
        LogManager.getLogger().info("Clearing cache for ${uuid ?: "all players"}")
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