package net.inceptioncloud.dragonfly.mods.ege

import net.inceptioncloud.dragonfly.mods.core.DragonflyMod

object EnhancedGameExperienceMod : DragonflyMod("Enhanced Game Experience") {

    @JvmStatic
    var tcpNoDelay by option(true)
}