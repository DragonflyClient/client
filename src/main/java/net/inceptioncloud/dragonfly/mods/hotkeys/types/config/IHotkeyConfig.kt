package net.inceptioncloud.dragonfly.mods.hotkeys.types.config

import com.google.gson.JsonObject
import net.inceptioncloud.dragonfly.mods.hotkeys.HotkeysMod

interface IHotkeyConfig {

    fun toJsonObject(): JsonObject = HotkeysMod.controller.gson.toJsonTree(this).asJsonObject
}