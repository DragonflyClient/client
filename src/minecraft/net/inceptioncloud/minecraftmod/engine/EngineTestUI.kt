package net.inceptioncloud.minecraftmod.engine

import net.inceptioncloud.minecraftmod.engine.internal.WidgetColor
import net.inceptioncloud.minecraftmod.engine.widget.assembled.InputTextField
import net.minecraft.client.gui.GuiScreen
import java.awt.Color

class EngineTestUI : GuiScreen() {

    private val Color.widget: WidgetColor
        get() = WidgetColor(this)

    override fun initGui() {
        +InputTextField(
            x = 100.0,
            y = 100.0,
            color = Color.RED.widget
        ) id "text-input"
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawRect(0, 0, width, height, Color(50, 50, 50).rgb)
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

}