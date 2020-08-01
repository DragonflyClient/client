package net.inceptioncloud.dragonfly.overlay.hotaction

import com.google.common.eventbus.Subscribe
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.animation.post
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseCubic
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

    fun onExpire(hotAction: HotActionWidget) {
        hotAction.morph(duration = 70, easing = EaseCubic.IN_OUT) {
            x = -width - 5.0
        }?.post { _, _ ->
            IngameOverlay.buffer.content.remove("hot-action")
            displayNext()
        }?.start()
    }

    @Subscribe
    fun onKeyType(event: KeyStateChangeEvent) {
        println("Typed: " + event.key + " (" + Keyboard.isKeyDown(event.key) + ")")
    }

    @JvmStatic
    fun test() {
        queue(
            "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut",
            "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna " +
                    "aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores ",
            1000,
            listOf(
                Action("Save") { println("Save") },
                Action("Copy") { println("Copy") },
                Action("Open") { println("Open") },
                Action("Upload") { println("Upload") }
            )
        )
    }
}