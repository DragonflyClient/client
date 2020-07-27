package net.inceptioncloud.dragonfly.hotkeys.types

import net.inceptioncloud.dragonfly.hotkeys.Hotkey
import net.inceptioncloud.dragonfly.transition.number.SmoothDoubleTransition
import net.inceptioncloud.dragonfly.transition.supplier.ForwardBackward
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiNewChat
import net.minecraft.client.gui.GuiScreen.Companion.sendChatMessage
import net.minecraft.client.gui.ScaledResolution
import org.lwjgl.input.Keyboard
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

    var drawLine = true
    var direction = 0

    var transition = SmoothDoubleTransition.builder()
        .fadeIn(0).stay((time * 180).toInt()).fadeOut((time * 20).toInt())
        .start(0.0).end(1.0)
        .autoTransformator { direction }
        .reachEnd { actionPerformed() }
        .build()

    override fun actionPerformed() {
        sendChatMessage(message, false)

        if (!fadeOut) {
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
                }
            }, (delay * 1000).toLong())
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