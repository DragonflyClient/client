package net.inceptioncloud.dragonfly.cosmetics.logic

import com.google.gson.JsonObject
import kotlin.reflect.KClass

/**
 * Represents a list of [CosmeticData] obtained by fetching the cosmetics of a Minecraft user
 */
class CosmeticDataList : ArrayList<CosmeticData>()

/**
 * Represents a list of [JsonObject]s obtained by fetching the database models for the
 * available cosmetics
 */
class DatabaseModelList : ArrayList<JsonObject>()

/**
 * Represents data of a cosmetic item that is hold by a player
 *
 * @param config The configuration for the cosmetic item to be parsed by the specific
 * cosmetic types
 * @param cosmeticId Universal identifier for the specific cosmetic item. This value
 * specifies the type of the cosmetic.
 * @param cosmeticQualifier Unique qualifier for the cosmetic item hold by a player.
 * This value is unique for every owned cosmetic item.
 * @param enabled Whether the cosmetic item is enabled for the player.
 * @param minecraft The UUID of the Minecraft account to which the cosmetic item is bound
 */
data class CosmeticData(
    val config: JsonObject,
    val cosmeticId: Int,
    val cosmeticQualifier: String,
    val enabled: Boolean,
    val minecraft: String
) {
    /**
     * Cache for already converted [config]s.
     */
    var cache: MutableMap<KClass<*>, Any>? = null

    /**
     * Parses the [config] as a subtype of [CosmeticConfig] by passing the [config]
     * to the constructor of the [subtype][T].
     */
    inline fun <reified T : CosmeticConfig> parseConfig(): T {
        val c = T::class
        if (cache == null) {
            cache = mutableMapOf()
        } else if (cache!!.containsKey(c)) {
            return cache!![c] as T
        }

        return c.constructors.first().call(config)
    }
}