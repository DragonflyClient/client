package net.inceptioncloud.minecraftmod.engine

import net.inceptioncloud.minecraftmod.Dragonfly
import net.inceptioncloud.minecraftmod.engine.internal.WidgetColor
import net.inceptioncloud.minecraftmod.engine.widget.primitive.TextDisplay
import net.minecraft.client.gui.GuiScreen
import java.awt.Color

class EngineTestUI : GuiScreen() {
    val Color.widget: WidgetColor
        get() = WidgetColor(this)

    override fun initGui() {
        +TextDisplay(
            x = 10.0,
            y = 10.0,
            widgetColor = Color.YELLOW.widget,
            text = "This is my sample text!",
            dropShadow = false,
            fontRenderer = Dragonfly.fontDesign.title
        ) id "text"
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawRect(0, 0, width, height, Color(50, 50, 50).rgb)
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

}