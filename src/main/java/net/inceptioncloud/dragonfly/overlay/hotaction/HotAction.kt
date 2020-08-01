package net.inceptioncloud.dragonfly.overlay.hotaction

import com.google.common.eventbus.Subscribe
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.animation.post
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseCubic
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle
import net.inceptioncloud.dragonfly.event.control.KeyStateChangeEvent
import net.inceptioncloud.dragonfly.overlay.IngameOverlay
import org.lwjgl.input.Keyboard
import java.util.concurrent.LinkedBlockingQueue

object HotAction {

    private val queue = LinkedBlockingQueue<HotActionWidget>()

    fun queue(title: String, message: String, duration: Int, actions: List<Action>) {
        queue.offer(HotActionWidget(title, message, duration, actions))
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
        val current = IngameOverlay.buffer["hot-action"] as? HotActionWidget ?: return

        if (event.press && event.key != Keyboard.KEY_LCONTROL && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
            val target = when(event.key) {
                Keyboard.KEY_1 -> 1
                Keyboard.KEY_2 -> 2
                Keyboard.KEY_3 -> 3
                Keyboard.KEY_4 -> 4
                Keyboard.KEY_5 -> 5
                else -> return
            }

            current.actions.getOrNull(target - 1)?.perform?.let {
                it.invoke(current)
                if (!current.allowMultipleActions) {
                    onExpire(current)
                }
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
            )
        )
    }
}