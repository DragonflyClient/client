package net.inceptioncloud.dragonfly.apps.spotifyintergration

import khttp.get
import net.inceptioncloud.dragonfly.Dragonfly
import org.apache.logging.log4j.LogManager

class SpotifyManager {

    private val prefix = "[Spotify Manager]"
    private val account = Dragonfly.account

    fun pausePlayback() {

        if(account == null) {
            LogManager.getLogger().info("$prefix User is not logged in")
            return
        }

        val response = get(
            url = "http://127.0.0.1:8080/pause",
            params = mapOf(
                "token" to account.token!!
            )
        )

        if(response.statusCode == 200) {
            LogManager.getLogger().info("$prefix PauseRequest was successful")
        }else {
            throw Exception("${response.statusCode} ${response.text}")
        }

    }

}

