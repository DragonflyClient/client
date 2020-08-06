package net.inceptioncloud.dragonfly.hotkeys.types

import net.inceptioncloud.dragonfly.hotkeys.Hotkey
import net.inceptioncloud.dragonfly.hotkeys.HotkeyController
import net.inceptioncloud.dragonfly.transition.number.SmoothDoubleTransition
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiScreen.Companion.sendChatMessage
import net.minecraft.client.gui.ScaledResolution
import java.awt.Color
import java.util.*

class HotkeyTypeChat(
    override val key: Int,
    override val modifierKey: Int?,
    override val time: Double,
    override val delay: Double,
    override val color: Color,
    val fadeOut: Boolean,
    val message: String
) :
    Hotkey() {

    /**
     * This boolean is used to apply the delay on the 'draw' function
     */
    var drawLine = true

    /**
     * Int which is used as Integer Supplier
     */
    var direction = 0

    /**
     * This Thread is used to apply the delay after the fadeOut animation (asynchronous)
     */
    lateinit var asyncDelay: Thread

    /**
     * Hold-Time progress bar animation
     */
    var transition = SmoothDoubleTransition.builder()
        .fadeIn(0).stay((time * 180).toInt()).fadeOut((time * 20).toInt())
        .start(0.0).end(1.0)
        .autoTransformator { direction }
        .reachEnd { actionPerformed() }
        .build()

    override fun actionPerformed() {
        sendChatMessage(message, false)
        HotkeyController.blockedHotkeys.add(this)

        if(modifierKey != null) {
            direction = -1
        }

        if (!fadeOut) {
            activateDelay()
        } else {
            asyncDelay = Thread {
                while (true) {
                    if (transition.isAtStart) {
                        activateDelay()
                    }
                }
            }

            asyncDelay.start()
        }
    }

    /**
     * Function which apply the delay
     */
    fun activateDelay() {
        transition.destroy()
        drawLine = false

        Timer().schedule(object : TimerTask() {
            override fun run() {
                transition = SmoothDoubleTransition.builder()
                    .fadeIn(0).stay((time * 180).toInt()).fadeOut((time * 20).toInt())
                    .start(0.0).end(1.0)
                    .autoTransformator { direction }
                    .reachEnd { actionPerformed() }
                    .build()
                drawLine = true
                HotkeyController.blockedHotkeys.remove(this@HotkeyTypeChat)
            }
        }, (delay * 1000).toLong())

        if (fadeOut) {
            asyncDelay.stop()
            asyncDelay.interrupt()
        }
    }

    override fun draw() {
        val scaledResolution = ScaledResolution(Minecraft.getMinecraft())
        val scaledWidth = scaledResolution.scaledWidth
        val scaledHeight = scaledResolution.scaledHeight

        if (drawLine) {
            Gui.drawRect(
                0.0,
                scaledHeight - 1.toDouble(),
                transition.get() * scaledWidth,
                scaledHeight.toDouble(),
                color.rgb
            )
        }
    }

}