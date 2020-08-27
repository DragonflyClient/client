package net.inceptioncloud.dragonfly.cosmetics

class CapeManager {

    val capes = hashMapOf<String, String>()

    init {
        capes["jxshuaa"] = "https://cdn.icnet.dev/dragonfly-files/capes/free_kill_white.png"
        capes["dyvs"] = "https://cdn.icnet.dev/dragonfly-files/capes/free_kill_black.png"
    }

    fun getCapeURLByUsername(username: String): String {

        if(capes.containsKey(username)) {
            return capes[username]!!
        }

        return ""
    }

}