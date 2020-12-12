package net.inceptioncloud.dragonfly.apps.spotifyintergration.backend

import khttp.get
import net.inceptioncloud.dragonfly.Dragonfly
import net.minecraft.client.Minecraft
import org.apache.logging.log4j.LogManager
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser

class SpotifyManager {

    private val prefix = "[Spotify Manager]"
    private val url = "http://127.0.0.1:8080/"
    private val account = Dragonfly.account

    var isPlaying = false
    var loop = "OFF"
    var title = "Nothing playing"
    var artist = ""
    var imageUrl = ""
    var songMax: Long = 0L
    var songCur: Long = 0L

    fun performDoAction(action: SpotifyDoAction, parameter: String?) {
        val thread = Thread {

            if (account == null) {
                LogManager.getLogger().info("$prefix User is not logged in")
                Thread.currentThread().interrupt()
            } else {
                val response = get(
                    url = "$url${action.route}",
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
                    Thread.currentThread().interrupt()
                } else {
                    throw Exception("${response.statusCode}: ${response.text}")
                }
            }
        }
        thread.name = "Spotify Integration Do Action"
        thread.start()
    }

    fun performGetAction(action: SpotifyGetAction, parameter: String?, perform: (String?) -> Unit) {
        val thread = Thread {
            if (account == null) {
                LogManager.getLogger().info("$prefix User is not logged in")
                perform(null)
                Thread.currentThread().interrupt()
            } else {
                val response = get(
                    url = "$url${action.route}",
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
                    Thread.currentThread().interrupt()
                } else {
                    throw Exception("${response.statusCode}: ${response.text}")
                }
            }
        }
        thread.name = "Spotify Integration Get Action"
        thread.start()
    }

    fun startUpdating() {
        println("Started Updating!")

        Thread {
            while (true) {
                performGetAction(SpotifyGetAction.CURRENT, null) {
                    val respond = JSONParser().parse(it) as JSONObject
                    this.title = respond["name"].toString()
                    this.artist = (respond["artists"].toString().toList()).first().toString()
                    this.songMax = respond["duration"].toString().toLong()
                    this.songCur = respond["progress"].toString().toLong()
                    this.isPlaying = respond["isPlaying"].toString().toBoolean()
                    this.imageUrl = respond["imageUrl"].toString()

                    var active = true

                    Thread {
                        do {
                            if (songCur != songMax) {
                                if (isPlaying) {
                                    songCur++
                                }
                            } else {
                                active = false
                            }
                            Thread.sleep(1000)
                        } while (active)
                    }.start()

                }

                Minecraft.getMinecraft().ingameMenuGUI.reloadSpotifyOverlay()

                Thread.sleep(3000)
            }
        }.start()

    }

}

