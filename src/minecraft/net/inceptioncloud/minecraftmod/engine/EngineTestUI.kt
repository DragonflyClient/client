package net.inceptioncloud.minecraftmod.engine

import net.inceptioncloud.minecraftmod.engine.animation.`in`.FloatAnimationIn
import net.inceptioncloud.minecraftmod.engine.animation.out.FloatAnimationOut
import net.inceptioncloud.minecraftmod.engine.internal.WidgetColor
import net.inceptioncloud.minecraftmod.engine.sequence.easing.EaseBack
import net.inceptioncloud.minecraftmod.engine.widget.Rectangle
import net.minecraft.client.gui.GuiScreen
import java.awt.Color

class EngineTestUI : GuiScreen()
{
    private val rectangle = Rectangle(100.0, 100.0, 100.0, 100.0, WidgetColor(Color(0x0062FF)))

    override fun initGui()
    {
        +rectangle
            .attachAnimation(FloatAnimationIn(200, 50.0, EaseBack.IN_OUT)) {
                post(FloatAnimationOut(200, 50.0, EaseBack.IN_OUT))
                start()
                attach()
            }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float)
    {
        drawRect(0, 0, width, height, Color(40, 40, 40, 255).rgb)

        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int)
    {
        rectangle.findAnimation(FloatAnimationOut::class.java)?.apply { start() }
        super.mouseClicked(mouseX, mouseY, mouseButton)
    }
}