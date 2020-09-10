package net.inceptioncloud.dragonfly.utils

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.net.URL

const val PROFILE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/"

/**
 * Performs a request to the Mojang servers.
 */
class MojangRequest {

    /**
     * UUID property that must be specified if required by the request.
     */
    lateinit var uuid: String

    /**
     * Sets the [uuid] property.
     */
    fun withUUID(uuid: String) = apply { this.uuid = uuid }

    /**
     * Fetches the name of the player specified by the [uuid].
     */
    fun getName(): String {
        return getProfile().get("name").asString
    }

    /**
     * Fetches the profile of the player specified by the [uuid].
     */
    fun getProfile(): JsonObject {
        val url = URL(PROFILE_URL + uuid)
        val text = url.readText()
        return JsonParser().parse(text).asJsonObject
    }
}