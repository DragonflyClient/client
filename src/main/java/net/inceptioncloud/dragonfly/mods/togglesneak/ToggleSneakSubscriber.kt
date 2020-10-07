package net.inceptioncloud.dragonfly.mods.togglesneak

import com.google.common.eventbus.Subscribe
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseQuad
import net.inceptioncloud.dragonfly.event.control.KeyInputEvent
import net.inceptioncloud.dragonfly.mods.keystrokes.KeystrokesMod
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager.color

object ToggleSneakSubscriber {

    @Subscribe
    fun keyPressed(event: KeyInputEvent) {
        if (Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().currentScreen == null) {
            if (event.key == Minecraft.getMinecraft().gameSettings.keyBindSneak.keyCode) {
                if (event.press) {
                    ToggleSneakMod.doSneak = !ToggleSneakMod.doSneak
                    Minecraft.getMinecraft().ingameGUI.initInGameOverlay()
                }
            }else if (event.key == Minecraft.getMinecraft().gameSettings.keyBindSprint.keyCode) {
                if (event.press) {
                    ToggleSneakMod.doSprint = !ToggleSneakMod.doSprint
                    Minecraft.getMinecraft().ingameGUI.initInGameOverlay()
                }
            }
        }
    }

}