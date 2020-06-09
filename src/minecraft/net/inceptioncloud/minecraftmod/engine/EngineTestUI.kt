package net.inceptioncloud.minecraftmod.engine

import net.inceptioncloud.minecraftmod.Dragonfly
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
            width = 200.0,
            label = "Hotkey Message"
        ) id "text-input"
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {

        val text = "Hello!?."
        val w1 = Dragonfly.fontDesign.defaultFont.fontRenderer().drawString(text, 0, 50, Color.WHITE.rgb)
        val w2 = Dragonfly.fontDesign.defaultFont.fontRenderer().getStringWidth(text)

        println("1: $w1")  // 183
        println("2: $w2")  // 188

        drawRect(0, 0, width, height, Color(255, 255, 255).rgb)
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

}