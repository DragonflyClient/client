package net.inceptioncloud.dragonfly.cosmetics.logic

import com.google.gson.JsonObject
import kotlin.reflect.KClass

class CosmeticDataList : ArrayList<CosmeticData>()

class DatabaseModelList : ArrayList<JsonObject>()

data class CosmeticData(
    val config: JsonObject,
    val cosmeticId: Int,
    val cosmeticQualifier: String,
    val enabled: Boolean,
    val minecraft: String
) {
    var convertedCache: MutableMap<KClass<*>, Any>? = null

    inline fun <reified T> parseConfig(): T {
        val c = T::class
        if (convertedCache == null) {
            convertedCache = mutableMapOf()
        } else if (convertedCache!!.containsKey(c)) {
            return convertedCache!![c] as T
        }

        return c.constructors.first().call(config)
    }
}