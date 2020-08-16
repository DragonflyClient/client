package net.inceptioncloud.dragonfly.engine.inspector

import com.google.common.eventbus.Subscribe
import net.inceptioncloud.dragonfly.event.control.KeyInputEvent
import net.inceptioncloud.dragonfly.subscriber.DefaultSubscribers
import net.minecraft.client.gui.GuiScreen.Companion.isCtrlKeyDown
import net.minecraft.client.gui.GuiScreen.Companion.isShiftKeyDown
import org.lwjgl.input.Keyboard.KEY_I

/**
 * A [default subscriber][DefaultSubscribers] that uses the [KeyInputEvent] to open the
 * inspector when the dedicated key combination is pressed.
 *
 * The default key combination (known from browsers) is `Ctrl+Shift+I`. As soon as the `I`
 * key is pressed while the two modifiers are hold down, [InspectorService.launch] is called to
 * launch or re-open the inspector.
 */
object InspectorSubscriber {

    @Subscribe
    fun onKeyType(event: KeyInputEvent) {
        with(event) {
            if (press && key == KEY_I && isCtrlKeyDown && isShiftKeyDown) {
                InspectorService.launch()
            }
        }
    }
}