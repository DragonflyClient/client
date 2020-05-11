package net.inceptioncloud.minecraftmod.engine

import net.inceptioncloud.minecraftmod.engine.internal.Alignment
import net.inceptioncloud.minecraftmod.engine.internal.WidgetColor
import net.inceptioncloud.minecraftmod.engine.widget.FilledCircle
import net.minecraft.client.gui.GuiScreen
import java.awt.Color

class EngineTestUI : GuiScreen()
{
    override fun initGui()
    {
        + FilledCircle(
            x = this@EngineTestUI.width / 2.0,
            y = this@EngineTestUI.height / 2.0,
            size = 150.0,
            horizontalAlignment = Alignment.CENTER,
            verticalAlignment = Alignment.CENTER,
            widgetColor = WidgetColor(255, 0, 0)
        ) id "test-circle"

        + FilledCircle(
            x = this@EngineTestUI.width / 2.0,
            y = this@EngineTestUI.height / 2.0,
            size = 4.0,
            horizontalAlignment = Alignment.CENTER,
            verticalAlignment = Alignment.CENTER,
            widgetColor = WidgetColor(200, 200, 50)
        ) id "center-point"
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float)
    {
        drawRect(0, 0, width, height, Color(40, 40, 40, 255).rgb)

        super.drawScreen(mouseX, mouseY, partialTicks)
    }

}