package net.inceptioncloud.minecraftmod.engine

import net.inceptioncloud.minecraftmod.engine.animation.`in`.FadeAnimationIn
import net.inceptioncloud.minecraftmod.engine.internal.Color2D
import net.inceptioncloud.minecraftmod.engine.shapes.Rectangle
import net.minecraft.client.gui.GuiScreen
import java.awt.Color

class EngineTestUI : GuiScreen()
{
    private val rectangle = Rectangle().static(10.0, 10.0, 50.0, 50.0, Color2D(Color.WHITE))

    private val animation = FadeAnimationIn(true)

    override fun initGui()
    {
        +rectangle.pushAnimation(animation)
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float)
    {
        drawRect(0, 0, width, height, Color.RED.rgb)

        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int)
    {
        animation.start()
        super.mouseClicked(mouseX, mouseY, mouseButton)
    }
}