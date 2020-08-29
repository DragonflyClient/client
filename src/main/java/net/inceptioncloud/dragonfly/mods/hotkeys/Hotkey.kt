package net.inceptioncloud.dragonfly.mods.hotkeys

import net.inceptioncloud.dragonfly.mods.hotkeys.types.data.HotkeyData
import net.inceptioncloud.dragonfly.transition.number.SmoothDoubleTransition
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.ScaledResolution
import java.util.*

abstract class Hotkey(val data: HotkeyData) {

    /**
     * Whether the hotkey is currently on delay and thus cannot be activated.
     */
    var isOnDelay = false

    /**
     * Hold-Time progress bar animation
     */
    var transition: SmoothDoubleTransition = SmoothDoubleTransition.builder()
        .fadeIn(0).stay((data.time * 180).toInt()).fadeOut((data.time * 20).toInt())
        .start(0.0).end(1.0)
        .reachEnd { execute() }
        .build()

    /**
     * Whether the transition should be recreated when set forward the next time.
     */
    private var shouldRecreateTransition = false

    /**
     * Function which is called after the time has expired (on key/keys hold)
     */
    protected abstract fun actionPerformed()

    /**
     * Calls the [actionPerformed] function that is implemented by the specific hotkey
     * and executes other necessary logic.
     */
    private fun execute() {
        if (isOnDelay) return

        actionPerformed()
        activateDelay()

        if (data.fadeOut) {
            progressBackward()
            shouldRecreateTransition = true
        } else {
            recreateTransition()
        }
    }

    /**
     * Function which apply the delay
     */
    private fun activateDelay() {
        if (isOnDelay) return

        isOnDelay = true

        Timer().schedule(object : TimerTask() {
            override fun run() {
                isOnDelay = false
            }
        }, (data.delay * 1000).toLong())
    }

    /**
     * Function which is called while the key/keys are hold,
     * this function is used to draw an animation or something else
     */
    fun drawProgress() {
        val scaledResolution = ScaledResolution(Minecraft.getMinecraft())
        val scaledWidth = scaledResolution.scaledWidth
        val scaledHeight = scaledResolution.scaledHeight

        Gui.drawRect(
            0.0,
            scaledHeight - 1.toDouble(),
            transition.get() * scaledWidth,
            scaledHeight.toDouble(),
            data.color.rgb
        )
    }

    /**
     * Progresses forward in the hotkey activation process checking whether the hotkey [isOnDelay].
     */
    fun progressForward() {
        if (isOnDelay) return
        if (shouldRecreateTransition) recreateTransition()

        transition.setForward()
    }

    /**
     * Progresses backward in the hotkey activation process.
     */
    fun progressBackward() {
        transition.setBackward()
    }

    /**
     * Recreates and resets the transition.
     */
    private fun recreateTransition() {
        transition.destroy()
        transition = SmoothDoubleTransition.builder()
            .fadeIn(0).stay((data.time * 180).toInt()).fadeOut((data.time * 20).toInt())
            .start(0.0).end(1.0)
            .reachEnd { execute() }
            .build()
        shouldRecreateTransition = false
    }
}