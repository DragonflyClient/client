package net.inceptioncloud.dragonfly.apps.spotifyintergration.backend

enum class SpotifyDoAction(val route: String, val parameterName: String? = null) {

    LOOP("loop","repeatState"),
    NEXT("next"),
    PAUSE("pause"),
    PLAY("play"),
    PREVIOUS("previous"),
    SHUFFLE("shuffle","value"),
    SEEK("seek","time")



}