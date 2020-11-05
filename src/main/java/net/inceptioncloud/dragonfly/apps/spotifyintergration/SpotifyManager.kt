package net.inceptioncloud.dragonfly.apps.spotifyintergration

import khttp.get
import net.inceptioncloud.dragonfly.Dragonfly
import org.apache.logging.log4j.LogManager

class SpotifyManager {

    private val prefix = "[Spotify Manager]"
    private val account = Dragonfly.account

    fun performDoAction(action: SpotifyDoAction, parameter: String?) {

        if(account == null) {
            LogManager.getLogger().info("$prefix User is not logged in")
            return
        }

        val response = get(
            url = "http://127.0.0.1:8080/${action.route}",
            params = if(action.parameterName != null) {
                mapOf(
                    "token" to account.token!!,
                    action.parameterName to parameter!!
                )
            }else {
                mapOf("token" to account.token!!)
            }
        )

        if(response.statusCode == 200) {
            LogManager.getLogger().info("$prefix ${action.route.toUpperCase()}Request was successful")
        }else {
            throw Exception("${response.statusCode} ${response.text}")
        }

    }

}

