package net.inceptioncloud.dragonfly.mods.keystrokes

import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.minecraft.client.Minecraft

class KeyStroke(val keyCode: Int, val keyDesc: String) {

    var pressed: Boolean = false
        set(value) {
            field = value

            Minecraft.getMinecraft().ingameGUI.stage["keystrokes-$keyDesc"].apply {
                if(this is TextField) {
                    if(value) {
                        Minecraft.getMinecraft().ingameGUI.keyStrokesTextColor["keystrokes-$keyDesc"] = KeyStrokesManager.colorTextActive
                        Minecraft.getMinecraft().ingameGUI.keyStrokesBackgroundColor["keystrokes-$keyDesc"] = KeyStrokesManager.colorBgActive
                    }else {
                        Minecraft.getMinecraft().ingameGUI.keyStrokesTextColor["keystrokes-$keyDesc"] = KeyStrokesManager.colorTextInactive
                        Minecraft.getMinecraft().ingameGUI.keyStrokesBackgroundColor["keystrokes-$keyDesc"] = KeyStrokesManager.colorBgInactive
                    }
                    Minecraft.getMinecraft().ingameGUI.initKeyStrokes(false)
                }
            }

        }

}