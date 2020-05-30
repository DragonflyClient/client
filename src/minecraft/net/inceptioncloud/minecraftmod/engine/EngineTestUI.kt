package net.inceptioncloud.minecraftmod.engine

import net.inceptioncloud.minecraftmod.Dragonfly
import net.inceptioncloud.minecraftmod.engine.internal.WidgetColor
import net.inceptioncloud.minecraftmod.engine.widget.primitive.TextRenderer
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import java.awt.Color

class EngineTestUI : GuiScreen() {
    val Color.widget: WidgetColor
        get() = WidgetColor(this)

    override fun initGui() {
        +TextRenderer(
            text = "InceptionCloud | This is a sample text!",
            x = 20.0,
            y = 20.0,
            widgetColor = Color.YELLOW.widget,
            fontRenderer = Dragonfly.fontDesign.regular
        ) id "sample-text-glyph"

        +TextRenderer(
            text = "InceptionCloud | This is a sample text!",
            x = 120.0,
            y = 20.0,
            widgetColor = Color.GREEN.widget,
            fontRenderer = Minecraft.getMinecraft().fontRendererObj
        ) id "sample-text-mc"
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawRect(0, 0, width, height, Color(50, 50, 50).rgb)
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

}