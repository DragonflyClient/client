package net.inceptioncloud.dragonfly.keystrokes

import net.inceptioncloud.dragonfly.engine.internal.WidgetColor
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.minecraft.client.Minecraft

class KeyStroke(val keyCode: Int, val keyDesc: String) {

    var pressed: Boolean = false
        set(value) {
            field = value

            Minecraft.getMinecraft().ingameGUI.stage["keystrokes-$keyDesc"].apply {
                if(this is TextField) {
                    if(value) {
                        Minecraft.getMinecraft().ingameGUI.keyStrokesTextColor = WidgetColor(1.0, 1.0, 1.0, 1.0)
                        Minecraft.getMinecraft().ingameGUI.keyStrokesBackgroundColor = WidgetColor(1.0, 1.0, 1.0, 0.2)
                    }else {
                        Minecraft.getMinecraft().ingameGUI.keyStrokesTextColor = WidgetColor(1.0, 1.0, 1.0, 1.0)
                        Minecraft.getMinecraft().ingameGUI.keyStrokesBackgroundColor = WidgetColor(0.5, 0.5, 0.5, 0.2)
                    }
                    this.backgroundColor = Minecraft.getMinecraft().ingameGUI.keyStrokesBackgroundColor
                    this.color = Minecraft.getMinecraft().ingameGUI.keyStrokesTextColor
                }
            }

        }

}