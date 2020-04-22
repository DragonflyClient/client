package net.inceptioncloud.minecraftmod.engine

import net.inceptioncloud.minecraftmod.engine.shapes.Rectangle
import net.minecraft.client.gui.GuiScreen
import java.awt.Color

class EngineTestUI : GuiScreen()
{
    override fun initGui()
    {
        +Rectangle().dynamic {
            x = (System.currentTimeMillis() / 100.0) % 20
            y = (System.currentTimeMillis() / 100.0) % 20
            width = 50.0
            height = 50.0
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float)
    {
        drawRect(0, 0, width, height, Color.RED.rgb)

        super.drawScreen(mouseX, mouseY, partialTicks)
    }
}