package net.inceptioncloud.dragonfly.apps.spotifyintergration

enum class SpotifyDoAction(val route: String, val parameterName: String?) {

    LOOP("loop","repeatState"),
    NEXT("next",null),
    PAUSE("pause",null),
    PLAY("play",null),
    PREVIOUS("previous",null),
    SHUFFLE("shuffle","value")



}