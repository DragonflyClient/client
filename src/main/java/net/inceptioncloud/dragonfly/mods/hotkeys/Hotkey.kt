package net.inceptioncloud.dragonfly.mods.hotkeys

import net.inceptioncloud.dragonfly.mods.hotkeys.types.ChatHotkey
import net.inceptioncloud.dragonfly.mods.hotkeys.types.data.HotkeyData
import net.inceptioncloud.dragonfly.transition.number.SmoothDoubleTransition
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.gui.ScaledResolution
import org.lwjgl.input.Keyboard
import java.util.*

abstract class Hotkey(val data: HotkeyData) {

    /**
     * Whether the hotkey is currently on delay and thus cannot be activated.
     */
    var isOnDelay = false

    /**
     * Whether the transition is currently in its fading out state. This means that the
     * hotkey should not be able to be activated.
     */
    private var inFadeOut = false

    /**
     * Hold-Time progress bar animation
     */
    var transition: SmoothDoubleTransition = SmoothDoubleTransition.builder()
        .fadeIn(0).stay((data.time * 180).toInt()).fadeOut((data.time * 20).toInt())
        .start(0.0).end(1.0)
        .reachEnd { execute() }
        .build()

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

        if (data.time != 0.0) {
            progressBackward()
            inFadeOut = true
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
        if(this is ChatHotkey && Minecraft.getMinecraft().currentScreen is GuiChat) return println("A")
        if (isOnDelay) return println("B") // don't activate when on delay
        if (inFadeOut && !transition.isAtStart) return println("C") // don't activate during fade out

        inFadeOut = false

        if (data.time == 0.0) {
            execute()
        } else {
            transition.setForward()
        }
    }

    /**
     * Progresses backward in the hotkey activation process.
     */
    fun progressBackward() {
        if (data.time != 0.0)
            transition.setBackward()
    }

    /**
     * Returns true if the state of the modifier keys are the same as they are expected by the
     * hotkey configuration to be.
     */
    fun areModifiersSatisfied(): Boolean = with(data) {
        if (requireCtrl != Keyboard.KEY_LCONTROL.isKeyPressed()) return false
        if (requireShift != Keyboard.KEY_LSHIFT.isKeyPressed()) return false
        if (requireAlt != Keyboard.KEY_LMENU.isKeyPressed()) return false

        return true
    }
}