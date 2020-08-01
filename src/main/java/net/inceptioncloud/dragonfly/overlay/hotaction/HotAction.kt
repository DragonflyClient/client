package net.inceptioncloud.dragonfly.overlay.hotaction

import com.google.common.eventbus.Subscribe
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.animation.post
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseCubic
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle
import net.inceptioncloud.dragonfly.event.control.KeyStateChangeEvent
import net.inceptioncloud.dragonfly.options.sections.OptionsSectionHotActions
import net.inceptioncloud.dragonfly.overlay.IngameOverlay
import org.lwjgl.input.Keyboard
import java.lang.IllegalStateException
import java.util.concurrent.LinkedBlockingQueue

object HotAction {

    private val queue = LinkedBlockingQueue<HotActionWidget>()

    fun queue(title: String, message: String, duration: Int, actions: List<Action>, allowMultipleActions: Boolean) {
        queue.offer(HotActionWidget(title, message, duration, actions, allowMultipleActions))
        displayNext()
    }

    fun displayNext() {
        if (IngameOverlay.buffer["hot-action"] != null || queue.isEmpty())
            return

        val next = queue.poll()

        next.updateStructure()
        next.x = -next.width - 5.0
        next.updateStructure()

        IngameOverlay.addComponent("hot-action", next)
        next.morph(duration = 70, easing = EaseCubic.IN_OUT) {
            x = 0.0
        }?.start()
    }

    fun onExpire(hotAction: HotActionWidget): Unit = with(hotAction) {
        expired = true
        getWidget<Rectangle>("timer")?.isVisible = false
        morph(duration = 70, easing = EaseCubic.IN_OUT) {
            x = -width - 5.0
        }?.post { _, _ ->
            IngameOverlay.buffer.content.remove("hot-action")
            displayNext()
        }?.start()
    }

    @Subscribe
    fun onKeyType(event: KeyStateChangeEvent) {
        if (!event.press)
            return

        val current = IngameOverlay.buffer["hot-action"] as? HotActionWidget ?: return
        val target = getSelectedAction(event.key) ?: return

        current.actions.getOrNull(target - 1)?.perform?.let {
            event.isCancelled = true
            it.invoke(current)

            if (!current.allowMultipleActions) {
                onExpire(current)
            }
        }
    }

    @JvmStatic
    fun test() {
        queue(
            "Screenshot Utilities",
            "A screenshot has been created! Do you wish to take further actions?",
            2000,
            listOf(
                Action("Save") { println("Save") },
                Action("Copy") { println("Copy") },
                Action("Open") { println("Open") },
                Action("Upload") { println("Upload") }
            ),
            true
        )
    }

    private fun getSelectedAction(key: Int): Int? {
        val triggerMode = OptionsSectionHotActions.triggerMode.key.get()
        return if (triggerMode == 0) when (key) {
            Keyboard.KEY_F7 -> 1
            Keyboard.KEY_F8 -> 2
            Keyboard.KEY_F9 -> 3
            Keyboard.KEY_F10 -> 4
            else -> null
        } else if (triggerMode == 1 && checkModernTrigger()) when (key) {
            Keyboard.KEY_1 -> 1
            Keyboard.KEY_2 -> 2
            Keyboard.KEY_3 -> 3
            Keyboard.KEY_4 -> 4
            else -> null
        } else null
    }

    private fun getTriggerKey(triggerKeyOption: Int): List<Int> = when (triggerKeyOption) {
        0 -> listOf(Keyboard.KEY_LCONTROL)
        1 -> listOf(Keyboard.KEY_RCONTROL)
        2 -> listOf(Keyboard.KEY_LMENU)
        3 -> listOf(Keyboard.KEY_RMENU)
        4 -> listOf(Keyboard.KEY_LSHIFT)
        5 -> listOf(Keyboard.KEY_RSHIFT)
        else -> throw IllegalStateException()
    }

    private fun checkModernTrigger(): Boolean {
        val triggerKeyOption = OptionsSectionHotActions.triggerKey.key.get()
        return if (triggerKeyOption == 6) {
            (Keyboard.KEY_LCONTROL.isPressed || Keyboard.KEY_RCONTROL.isPressed) &&
                    (Keyboard.KEY_LMENU.isPressed || Keyboard.KEY_RMENU.isPressed)
        } else {
            getTriggerKey(triggerKeyOption).all { it.isPressed }
        }
    }

    private val Int.isPressed
        get() = Keyboard.isKeyDown(this)
}