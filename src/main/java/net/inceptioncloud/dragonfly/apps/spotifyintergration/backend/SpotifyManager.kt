package net.inceptioncloud.dragonfly.apps.spotifyintergration.backend

import com.google.gson.Gson
import khttp.get
import net.inceptioncloud.dragonfly.Dragonfly
import org.apache.logging.log4j.LogManager
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser

class SpotifyManager {

    init {

    }

    private val prefix = "[Spotify Manager]"
    private val account = Dragonfly.account

    var isPlaying = false
    var loop = "OFF"
    var title = ""
    var artist = ""
    var imageUrl = ""
    var songMax: Long = 216000L
    var songCur: Long = 87000L

    fun performDoAction(action: SpotifyDoAction, parameter: String?) {
        val thread = Thread {

            if (account == null) {
                LogManager.getLogger().info("$prefix User is not logged in")
            } else {
                val response = get(
                    url = "http://127.0.0.1:8080/${action.route}",
                    params = if (action.parameterName != null) {
                        mapOf(
                            "token" to account.token!!,
                            action.parameterName to parameter!!
                        )
                    } else {
                        mapOf("token" to account.token!!)
                    }
                )

                if (response.statusCode == 200) {
                    LogManager.getLogger().info("$prefix ${action.route.capitalize()}Request was successful")
                } else {
                    throw Exception("${response.statusCode}: ${response.text}")
                }
            }
        }
        thread.name = "Spotify Integration"
        thread.start()
    }

    fun performGetAction(action: SpotifyGetAction, parameter: String?, perform: (String?) -> Unit) {
        val thread = Thread {
            if (account == null) {
                LogManager.getLogger().info("$prefix User is not logged in")
                perform(null)
            } else {
                val response = get(
                    url = "http://127.0.0.1:8080/${action.route}",
                    params = if (action.parameterName != null) {
                        mapOf(
                            "token" to account.token!!,
                            action.parameterName to parameter!!
                        )
                    } else {
                        mapOf("token" to account.token!!)
                    }
                )

                if (response.statusCode == 200) {
                    LogManager.getLogger().info("$prefix ${action.route.capitalize()}Request was successful")
                    perform(response.text)
                } else {
                    throw Exception("${response.statusCode}: ${response.text}")
                }
            }
        }
        thread.name = "Spotify Integration"
        thread.start()
    }

    fun performDoAction(action: SpotifyDoAction) {}

    private fun update() {

        // TODO: Add songMax and isPlaying to backend

        Thread {
            Thread.sleep(10000)
            Dragonfly.spotifyManager.performGetAction(SpotifyGetAction.CURRENT, null) {
                val respond = JSONParser().parse(it) as JSONObject
                this.title = respond["name"] as String
                this.artist = (respond["artists"] as List<*>).first().toString()
                this.songCur = respond["progress"] as Long

                var active = true

                do {
                    if(songCur != songMax) {
                        if(isPlaying) {
                            songCur++
                        }
                    }else {
                        active = false
                    }
                    Thread.sleep(1000)
                }while(active)
            }
        }.start()
    }

}

