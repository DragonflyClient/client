package net.inceptioncloud.dragonfly.mods.hotkeys.types.data

import com.google.gson.JsonObject
import net.inceptioncloud.dragonfly.engine.internal.WidgetColor
import net.inceptioncloud.dragonfly.mods.hotkeys.Hotkey

/**
 * This class holds the data about a hotkey and is used as an interface between the hotkey code
 * and the stored hotkeys in the JSON format.
 *
 * @param key Primary key
 * @param modifierKey Secondary key, this key is not necessary if the user doesn't want a secondary key he can
 * leave this key empty, but if one is set the key has to be pressed first
 * @param time Time the key(s) have to be pressed until the hotkey is [executed][Hotkey.execute]
 * @param delay Delay until the user can use the hotkey again after the 'actionPerformed' function was called
 * @param color Color for the animation, ui or something else
 * @param fadeOut Whether the progress bar should fade out
 */
data class HotkeyData (
    val type: EnumHotkeyType,
    val key: Int,
    val modifierKey: Int?,
    val time: Double,
    val delay: Double,
    val color: WidgetColor,
    val fadeOut: Boolean,
    val config: JsonObject
)