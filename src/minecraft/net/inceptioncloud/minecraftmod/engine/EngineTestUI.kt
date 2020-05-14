package net.inceptioncloud.minecraftmod.engine

import net.inceptioncloud.minecraftmod.engine.animation.`in`.FloatAnimationIn
import net.inceptioncloud.minecraftmod.engine.internal.Alignment
import net.inceptioncloud.minecraftmod.engine.internal.WidgetColor
import net.inceptioncloud.minecraftmod.engine.sequence.easing.EaseBack
import net.inceptioncloud.minecraftmod.engine.widget.assembled.RoundedRectangle
import net.minecraft.client.gui.GuiScreen
import java.awt.Color

class EngineTestUI : GuiScreen()
{
    override fun initGui()
    {
        +RoundedRectangle(
            x = System.currentTimeMillis() % 100.0 + 50.0,
            y = 50.0,
            width = 200.0,
            height = 150.0,
            widgetColor = WidgetColor(Color.GREEN),
            arc = 20.0,
            horizontalAlignment = Alignment.START,
            verticalAlignment = Alignment.START
        ).attachAnimation(FloatAnimationIn(150, 30.0, EaseBack.IN_OUT)) {
            start()
            attach()
        } id "rounded-rectangle"
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float)
    {
        drawRect(0, 0, width, height, Color(40, 40, 40, 255).rgb)

        super.drawScreen(mouseX, mouseY, partialTicks)
    }

}