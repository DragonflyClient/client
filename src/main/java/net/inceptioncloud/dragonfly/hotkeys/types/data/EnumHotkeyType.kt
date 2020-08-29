package net.inceptioncloud.dragonfly.hotkeys.types.data

import net.inceptioncloud.dragonfly.hotkeys.types.ChatHotkey
import net.inceptioncloud.dragonfly.hotkeys.types.config.ChatHotkeyConfig
import kotlin.reflect.KClass

enum class EnumHotkeyType(
    val hotkeyClass: KClass<*>,
    val configClass: KClass<*>
) {

    CHAT(ChatHotkey::class, ChatHotkeyConfig::class)
}