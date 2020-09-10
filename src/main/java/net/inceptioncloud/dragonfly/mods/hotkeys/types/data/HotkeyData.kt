package net.inceptioncloud.dragonfly.mods.hotkeys.types.data

import com.google.gson.JsonObject
import net.inceptioncloud.dragonfly.engine.internal.WidgetColor
import net.inceptioncloud.dragonfly.mods.hotkeys.Hotkey
import net.inceptioncloud.dragonfly.utils.Keep

/**
 * This class holds the data about a hotkey and is used as an interface between the hotkey code
 * and the stored hotkeys in the JSON format.
 *
 * @param key Primary key
 * @param time Time the key(s) have to be pressed until the hotkey is [executed][Hotkey.execute]
 * @param delay Delay until the user can use the hotkey again after the 'actionPerformed' function was called
 * @param color Color for the animation, ui or something else
 */
@Keep
data class HotkeyData (
    val type: EnumHotkeyType,
    val key: Int,
    val requireCtrl: Boolean,
    val requireShift: Boolean,
    val requireAlt: Boolean,
    val time: Double,
    val delay: Double,
    val color: WidgetColor,
    val config: JsonObject
)