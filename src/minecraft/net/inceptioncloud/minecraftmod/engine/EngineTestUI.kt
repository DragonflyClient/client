package net.inceptioncloud.minecraftmod.engine

import net.inceptioncloud.minecraftmod.engine.internal.Alignment
import net.inceptioncloud.minecraftmod.engine.internal.WidgetColor
import net.inceptioncloud.minecraftmod.engine.widget.assembled.RoundedRectangle
import net.minecraft.client.gui.GuiScreen
import java.awt.Color

class EngineTestUI : GuiScreen()
{
    override fun initGui()
    {
        + RoundedRectangle(
            x = width / 2.0,
            y = height / 2.0,
            width = 200.0,
            height = 150.0,
            widgetColor = WidgetColor(Color.GREEN),
            arc = 20.0,
            horizontalAlignment = Alignment.CENTER,
            verticalAlignment = Alignment.CENTER
        ) id "rounded-rectangle"
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float)
    {
        drawRect(0, 0, width, height, Color(40, 40, 40, 255).rgb)

        super.drawScreen(mouseX, mouseY, partialTicks)
    }

}