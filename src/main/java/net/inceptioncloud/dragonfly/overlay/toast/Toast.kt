package net.inceptioncloud.dragonfly.overlay.toast

import kotlinx.coroutines.yield
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.animation.post
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseCubic
import net.inceptioncloud.dragonfly.options.sections.OptionsSectionOverlay
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
        if (!OptionsSectionOverlay.enableToastMessages.getKey().get())
            return

        queue.offer(ToastWidget().apply {
            this.text = title
            this.duration = duration
        })
        displayNext()
    }

    /**
     * Adds the next toast message in the [queue] (if available) to the [ScreenOverlay] while providing
     * a smooth fly-in animation
     */
    fun displayNext(): Boolean {
        if (ScreenOverlay.stage["toast"] != null || queue.isEmpty())
            return false

        val next = queue.poll()

        next.runStructureUpdate()
        next.y = ScreenOverlay.dimensions.height - 45.0
        next.runStructureUpdate()

        ScreenOverlay.addComponent("toast", next)

        next.morph(
            60,
            EaseCubic.IN_OUT,
            next::y to ScreenOverlay.dimensions.height - 85.0,
            next::opacity to 1.0
        )?.post { _, _ ->
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
        morph(
            60,
            EaseCubic.IN_OUT,
            ::y to ScreenOverlay.dimensions.height - 45.0,
            ::opacity to 0.0
        )?.post { _, _ ->
            ScreenOverlay.stage.remove("toast")
            displayNext()
        }?.start()
    }
}
