package net.inceptioncloud.dragonfly.cosmetics.logic

import com.google.gson.JsonObject

class CosmeticDataList : ArrayList<CosmeticData>()

class DatabaseModelList : ArrayList<JsonObject>()

data class CosmeticData(
    val config: JsonObject,
    val cosmeticId: Int,
    val cosmeticQualifier: String,
    val enabled: Boolean,
    val minecraft: String
)