package net.inceptioncloud.dragonfly.mods.hotkeys.types.data

import net.inceptioncloud.dragonfly.mods.hotkeys.types.ChatHotkey
import net.inceptioncloud.dragonfly.mods.hotkeys.types.config.ChatHotkeyConfig
import kotlin.reflect.KClass

enum class EnumHotkeyType(
    val hotkeyClass: KClass<*>,
    val configClass: KClass<*>
) {

    CHAT(ChatHotkey::class, ChatHotkeyConfig::class)
}