package net.inceptioncloud.dragonfly.mods.ege

import net.inceptioncloud.dragonfly.mods.core.DragonflyMod

object EnhancedGameExperienceMod : DragonflyMod("Enhanced Game Experience") {

    @JvmStatic
    var tcpNoDelay by option(true)
    var disableExplicitGC by option(false)

    @JvmStatic
    fun tryExplicitGC() {
        if (!disableExplicitGC) System.gc()
    }
}