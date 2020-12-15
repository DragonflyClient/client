package net.inceptioncloud.dragonfly.mods.hotkeys.types.config

import com.google.gson.JsonObject
import net.inceptioncloud.dragonfly.mods.hotkeys.HotkeysMod
import net.inceptioncloud.dragonfly.utils.Keep

@Keep
interface IHotkeyConfig {

    fun toJsonObject(): JsonObject = HotkeysMod.controller.gson.toJsonTree(this).asJsonObject
}