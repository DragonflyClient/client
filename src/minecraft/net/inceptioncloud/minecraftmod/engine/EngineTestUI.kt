package net.inceptioncloud.minecraftmod.engine

import net.inceptioncloud.minecraftmod.Dragonfly
import net.inceptioncloud.minecraftmod.engine.internal.WidgetColor
import net.inceptioncloud.minecraftmod.engine.widget.assembled.InputTextField
import net.inceptioncloud.minecraftmod.engine.widget.assembled.TextField
import net.minecraft.client.gui.GuiScreen
import java.awt.Color

class EngineTestUI : GuiScreen() {

    private val Color.widget: WidgetColor
        get() = WidgetColor(this)

    override fun initGui() {
        +InputTextField(
            x = 100.0,
            y = 100.0,
            width = 200.0,
            label = "Hotkey Message"
        ) id "text-input"

        +TextField(
            x = 100.0,
            y = 150.0,
            width = 100.0,
            height = 20.0,
            font = Dragonfly.fontDesign.defaultFont,
            fontSize = 20.0
        ) id "test-field"
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawRect(0, 0, width, height, Color(255, 255, 255).rgb)
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

}