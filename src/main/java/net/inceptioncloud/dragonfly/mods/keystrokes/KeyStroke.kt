package net.inceptioncloud.dragonfly.mods.keystrokes

import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.mods.KeystrokesMod
import net.minecraft.client.Minecraft

class KeyStroke(val keyCode: Int, val keyDesc: String) {

    var pressed: Boolean = false
        set(value) {
            field = value

            Minecraft.getMinecraft().ingameGUI.stage["keystrokes-$keyDesc"].apply {
                if(this is TextField) {
                    if(value) {
                        Minecraft.getMinecraft().ingameGUI.keyStrokesTextColor["keystrokes-$keyDesc"] = KeystrokesMod.textActiveColor
                        Minecraft.getMinecraft().ingameGUI.keyStrokesBackgroundColor["keystrokes-$keyDesc"] = KeystrokesMod.bgActiveColor
                    }else {
                        Minecraft.getMinecraft().ingameGUI.keyStrokesTextColor["keystrokes-$keyDesc"] = KeystrokesMod.textInactiveColor
                        Minecraft.getMinecraft().ingameGUI.keyStrokesBackgroundColor["keystrokes-$keyDesc"] = KeystrokesMod.bgInactiveColor
                    }
                    Minecraft.getMinecraft().ingameGUI.initKeyStrokes(false)
                }
            }

        }

}