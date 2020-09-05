package net.inceptioncloud.dragonfly.mods.hotkeys.types.config

import net.inceptioncloud.dragonfly.utils.Keep

@Keep
data class ChatHotkeyConfig(
    val message: String
) : IHotkeyConfig