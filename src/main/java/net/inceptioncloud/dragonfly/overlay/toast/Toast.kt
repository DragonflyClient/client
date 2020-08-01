package net.inceptioncloud.dragonfly.overlay.toast

import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.animation.post
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseBack
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseCubic
import net.inceptioncloud.dragonfly.overlay.ScreenOverlay
import net.minecraft.client.gui.GuiIngame
import java.util.concurrent.LinkedBlockingQueue

object Toast {

    /**
     * Contains all queued toast messages
     */
    private val queue = LinkedBlockingQueue<ToastWidget>()

    /**
     * Adds a new toast message with the specified properties to the [queue] and calls [displayNext]
     */
    fun queue(title: String, duration: Int) {
        queue.offer(ToastWidget(title, duration))
        displayNext()
    }

    /**
     * Adds the next toast message in the [queue] (if available) to the [ScreenOverlay] while providing
     * a smooth fly-in animation
     */
    fun displayNext(): Boolean {
        if (ScreenOverlay.buffer["toast"] != null || queue.isEmpty())
            return false

        val next = queue.poll()

        next.updateStructure()
        next.y = ScreenOverlay.dimensions.height + next.height
        next.updateStructure()

        ScreenOverlay.addComponent("toast", next)

        next.morph(duration = 100, easing = EaseBack.OUT) {
            y = ScreenOverlay.dimensions.height - 85.0
        }?.post { _, _ ->
            GuiIngame.canDisplayActionBar = false
        }?.start()

        return true
    }

    /**
     * Removes the given [toast] from the [ScreenOverlay] after finishing the fly-out transition
     * and calls [displayNext]
     */
    fun finish(toast: ToastWidget): Unit = with(toast) {
        if (queue.isEmpty())
            GuiIngame.canDisplayActionBar = true

        expired = true
        morph(duration = 100, easing = EaseBack.IN) {
            y = ScreenOverlay.dimensions.height + height
        }?.post { _, _ ->
            ScreenOverlay.buffer.content.remove("toast")
            displayNext()
        }?.start()
    }

    @JvmStatic
    fun test() {
        queue(
            "Your screenshot has been copied!",
            1000
        )
    }
}