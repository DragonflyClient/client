package net.inceptioncloud.dragonfly.mods.hotkeys.types.data

import net.inceptioncloud.dragonfly.mods.hotkeys.types.ChatHotkey
import net.inceptioncloud.dragonfly.mods.hotkeys.types.config.ChatHotkeyConfig
import net.inceptioncloud.dragonfly.utils.Keep
import kotlin.reflect.KClass

@Keep
enum class EnumHotkeyType(
    val hotkeyClass: KClass<*>,
    val configClass: KClass<*>
) {

    CHAT(ChatHotkey::class, ChatHotkeyConfig::class)
}