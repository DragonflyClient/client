package net.inceptioncloud.dragonfly.mods.keystrokes

import com.google.common.eventbus.Subscribe
import net.inceptioncloud.dragonfly.event.client.ResizeEvent
import net.inceptioncloud.dragonfly.event.client.ToggleFullscreenEvent
import net.inceptioncloud.dragonfly.event.control.KeyInputEvent
import net.inceptioncloud.dragonfly.event.control.MouseInputEvent
import net.minecraft.client.Minecraft

object KeystrokesSubscriber {

    @Subscribe
    fun keyPressed(event: KeyInputEvent) {
        for (keyStroke in KeystrokesManager.keystrokes) {
            if (keyStroke.keyCode == event.key) {
                keyStroke.pressed = event.press
            }
        }
    }

    @Subscribe
    fun buttonPressed(event: MouseInputEvent) {
        if (event.button == -1) return
        for (keyStroke in KeystrokesManager.keystrokes) {
            if (keyStroke.keyCode == event.button - 100) {
                keyStroke.pressed = event.press
            }
        }
    }

    @Subscribe
    fun toggleFullscreenWindow(event: ToggleFullscreenEvent) {
        Minecraft.getMinecraft().ingameGUI.initKeystrokes(true)
    }

    @Subscribe
    fun resizeWindow(event: ResizeEvent) {
        Minecraft.getMinecraft().ingameGUI.initKeystrokes(true)
    }

}