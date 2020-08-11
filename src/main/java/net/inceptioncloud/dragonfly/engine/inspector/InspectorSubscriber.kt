package net.inceptioncloud.dragonfly.engine.inspector

import com.google.common.eventbus.Subscribe
import net.inceptioncloud.dragonfly.event.control.KeyInputEvent
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Keyboard

object InspectorSubscriber {
    @Subscribe
    fun onKeyType(event: KeyInputEvent) {
        if (event.key == Keyboard.KEY_I && GuiScreen.isCtrlKeyDown && GuiScreen.isShiftKeyDown) {
            Inspector.launch()
        }
    }
}