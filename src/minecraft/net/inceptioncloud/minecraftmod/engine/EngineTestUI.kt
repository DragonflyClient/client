package net.inceptioncloud.minecraftmod.engine

import net.inceptioncloud.minecraftmod.engine.animation.`in`.FloatAnimationIn
import net.inceptioncloud.minecraftmod.engine.animation.out.FloatAnimationOut
import net.inceptioncloud.minecraftmod.engine.internal.WidgetColor
import net.inceptioncloud.minecraftmod.engine.sequence.easing.EaseBack
import net.inceptioncloud.minecraftmod.engine.shapes.Rectangle
import net.minecraft.client.gui.GuiScreen
import java.awt.Color

class EngineTestUI : GuiScreen()
{
    private val rectangle = Rectangle().static(100.0, 100.0, 100.0, 100.0, WidgetColor(Color.WHITE))

    override fun initGui()
    {
        +rectangle.pushAnimation(FloatAnimationIn(200, -50.0, EaseBack.IN_OUT))
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float)
    {
        drawRect(0, 0, width, height, Color.RED.rgb)

        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int)
    {
        val floatAnimation = rectangle.findAnimation(FloatAnimationIn::class.java)
        if (floatAnimation != null)
        {
            floatAnimation.start()
        } else
        {
            rectangle.pushAndStartAnimation(FloatAnimationOut(200, 50.0, EaseBack.IN_OUT))
        }

        super.mouseClicked(mouseX, mouseY, mouseButton)
    }
}