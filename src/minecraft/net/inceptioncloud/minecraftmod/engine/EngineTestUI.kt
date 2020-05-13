package net.inceptioncloud.minecraftmod.engine

import net.inceptioncloud.minecraftmod.engine.internal.Alignment
import net.inceptioncloud.minecraftmod.engine.internal.WidgetColor
import net.inceptioncloud.minecraftmod.engine.widget.Arc
import net.inceptioncloud.minecraftmod.engine.widget.Circle
import net.inceptioncloud.minecraftmod.engine.widget.FilledCircle
import net.minecraft.client.gui.GuiScreen
import java.awt.Color

class EngineTestUI : GuiScreen()
{
    override fun initGui()
    {
        + Arc(
            x = this@EngineTestUI.width / 2.0,
            y = this@EngineTestUI.height / 2.0,
            start = 90,
            end = 180,
            width = 100.0,
            height = 100.0,
            widgetColor = WidgetColor(255, 0, 0)
        ) id "test-arc"

        + Circle(
            x = this@EngineTestUI.width / 2.0,
            y = this@EngineTestUI.height / 2.0,
            size = 100.0,
            widgetColor = WidgetColor(Color.MAGENTA)
        ) id "test-circle"

        + FilledCircle(
            x = this@EngineTestUI.width / 2.0,
            y = this@EngineTestUI.height / 2.0,
            size = 2.0,
            horizontalAlignment = Alignment.CENTER,
            verticalAlignment = Alignment.CENTER,
            widgetColor = WidgetColor(0, 255, 0)
        ) id "center"
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float)
    {
        drawRect(0, 0, width, height, Color(40, 40, 40, 255).rgb)

        super.drawScreen(mouseX, mouseY, partialTicks)
    }

}