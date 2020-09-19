package net.inceptioncloud.dragonfly.utils

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.runBlocking
import net.inceptioncloud.dragonfly.apps.accountmanager.AccountManagerApp
import java.awt.image.BufferedImage
import java.net.URL
import javax.imageio.ImageIO

const val PROFILE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s"
const val SKULL_URL = "https://crafatar.com/avatars/%s?size=64&default=MHF_Steve&overlay=true"

/**
 * Performs a request to the Mojang servers.
 */
class MojangRequest {

    companion object {

        private val skullCache = mutableMapOf<String, BufferedImage?>()
    }

    /**
     * UUID property that must be specified if required by the request.
     */
    lateinit var uuid: String

    /**
     * Sets the [uuid] property.
     */
    fun withUUID(uuid: String) = apply { this.uuid = uuid }

    /**
     * Fetches the skull from the crafatar api. Note that this function uses a cache.
     */
    fun getSkull(): BufferedImage? {
        if (skullCache.containsKey(uuid)) return skullCache[uuid]!!

        val account = AccountManagerApp.accounts.firstOrNull { it.uuid.toString() == uuid }
        if (account != null) return runBlocking { account.retrieveSkull() }

        return kotlin.runCatching {
            ImageIO.read(URL(SKULL_URL.format(uuid)))
        }.getOrNull().also { skullCache[uuid] = it }
    }

    fun getSkull(block: (BufferedImage?) -> Unit) = apply { block(getSkull()) }

    /**
     * Fetches the name of the player specified by the [uuid].
     */
    fun getName(): String {
        return getProfile().get("name").asString
    }

    fun getName(block: (String) -> Unit) = apply { block(getName()) }

    /**
     * Fetches the profile of the player specified by the [uuid].
     */
    fun getProfile(): JsonObject {
        val url = URL(PROFILE_URL.format(uuid))
        val text = url.readText()
        return JsonParser().parse(text).asJsonObject
    }
}