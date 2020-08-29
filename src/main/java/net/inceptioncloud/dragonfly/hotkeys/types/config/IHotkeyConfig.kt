package net.inceptioncloud.dragonfly.hotkeys.types.config

import com.google.gson.JsonObject
import net.inceptioncloud.dragonfly.hotkeys.HotkeyController

interface IHotkeyConfig {

    fun toJsonObject(): JsonObject = HotkeyController.gson.toJsonTree(this).asJsonObject
}