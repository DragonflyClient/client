package net.inceptioncloud.dragonfly.mods.keystrokes

import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.minecraft.client.Minecraft

class Keystroke(val keyCode: Int, val keyDesc: String) {

    var pressed: Boolean = false
        set(value) {
            field = value

            val ingameGUI = Minecraft.getMinecraft().ingameGUI
            ingameGUI.stage["keystrokes-$keyDesc"].apply {
                if(this is TextField) {
                    if(value) {
                        ingameGUI.keystrokesTextColor["keystrokes-$keyDesc"] = KeystrokesMod.textActiveColor
                        ingameGUI.keystrokesBackgroundColor["keystrokes-$keyDesc"] = KeystrokesMod.bgActiveColor
                    } else {
                        ingameGUI.keystrokesTextColor["keystrokes-$keyDesc"] = KeystrokesMod.textInactiveColor
                        ingameGUI.keystrokesBackgroundColor["keystrokes-$keyDesc"] = KeystrokesMod.bgInactiveColor
                    }
                    ingameGUI.initKeystrokes(false)
                }
            }
        }

}