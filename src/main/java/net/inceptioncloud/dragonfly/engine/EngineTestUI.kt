package net.inceptioncloud.dragonfly.engine

import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Keyboard
import java.awt.Color

class EngineTestUI : GuiScreen() {
    override fun initGui() {
        Keyboard.enableRepeatEvents(true)
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawRect(0, 0, width, height, Color(255, 255, 255).rgb)
        drawRect(width / 2, 0, width, height, Color(0, 0, 0).rgb)
        super.drawScreen(mouseX, mouseY, partialTicks)
    }
}