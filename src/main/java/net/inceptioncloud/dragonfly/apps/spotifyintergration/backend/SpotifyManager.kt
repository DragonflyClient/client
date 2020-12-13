package net.inceptioncloud.dragonfly.apps.spotifyintergration.backend

import khttp.get
import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.apps.spotifyintergration.frontend.SpotifyOverlay
import net.minecraft.client.Minecraft
import org.apache.logging.log4j.LogManager
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.util.*

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


    var startedUpdating = false // Whether the updating of the spotify overlay of the IngameMenuUI is started or not
    var startedOverlayUpdating = false // Whether the updating of the spotify overlay of GuiIngame is started or not

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
        if (!startedUpdating) {
            println("Started Updating!")

            Thread {
                while (true) {
                    performGetAction(SpotifyGetAction.CURRENT, null) {
                        val respond = JSONParser().parse(it) as JSONObject
                        this.title = respond["name"].toString()
                        this.artist = respond["artists"].toString().split(",")[0]
                        this.songMax = respond["duration"].toString().toLong()
                        this.songCur = respond["progress"].toString().toLong()
                        this.isPlaying = respond["isPlaying"].toString().toBoolean()
                        this.imageUrl = respond["imageUrl"].toString()

                        if (!startedOverlayUpdating) {
                            Thread {
                                while (true) {
                                    if (songCur < songMax) {
                                        if (isPlaying) {
                                            songCur += 1000
                                        }
                                    }else {
                                        manualUpdate()
                                    }

                                    SpotifyOverlay.update()
                                    Minecraft.getMinecraft().ingameGUI.initInGameOverlay()

                                    println("Updated!")

                                    Thread.sleep(1000)
                                }
                            }.start()
                            startedOverlayUpdating = true
                        }

                    }

                    try {
                        Minecraft.getMinecraft().ingameMenuGUI.reloadSpotifyOverlay()
                    }catch (e: Exception) {}

                    Thread.sleep(10000)
                }
            }.start()
        }

        startedUpdating = true
    }

    fun filterTrackName(original: String): String {
        val first = original.split("(")[0]
        val second = first.split("-")[0]
        return second
    }

}

