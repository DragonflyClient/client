package net.inceptioncloud.dragonfly.engine

import net.inceptioncloud.dragonfly.engine.widgets.assembled.RoundedRectangle
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Keyboard
import java.awt.Color

class EngineTestUI : GuiScreen() {
    override fun initGui() {
        Keyboard.enableRepeatEvents(true)

        +RoundedRectangle {
            x = 50.0
            y = 50.0
            width = 85.0
            height = 37.0
        } id "test"

        GraphicsEngine.runAfter(5000) {
            getWidget<RoundedRectangle>("test")!!.x = 100.0
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawRect(0, 0, width, height, Color(0x1c1f2b).rgb)
        super.drawScreen(mouseX, mouseY, partialTicks)
    }
}