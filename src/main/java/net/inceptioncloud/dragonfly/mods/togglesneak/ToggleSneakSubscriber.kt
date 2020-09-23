package net.inceptioncloud.dragonfly.mods.togglesneak

import com.google.common.eventbus.Subscribe
import net.inceptioncloud.dragonfly.event.control.KeyInputEvent
import net.minecraft.client.Minecraft

object ToggleSneakSubscriber {

    @Subscribe
    fun keyPressed(event: KeyInputEvent) {
        if (Minecraft.getMinecraft().thePlayer != null) {
            if (event.key == Minecraft.getMinecraft().gameSettings.keyBindSneak.keyCode) {
                if (event.press) {
                    ToggleSneakMod.doSneak = !ToggleSneakMod.doSneak
                }
            }else if (event.key == Minecraft.getMinecraft().gameSettings.keyBindSprint.keyCode) {
                if (event.press) {
                    ToggleSneakMod.doSprint = !ToggleSneakMod.doSprint
                }
            }
        }
    }

}