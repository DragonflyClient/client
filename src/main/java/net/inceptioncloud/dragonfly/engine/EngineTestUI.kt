package net.inceptioncloud.dragonfly.engine

import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Keyboard
import java.awt.Color

class EngineTestUI : GuiScreen() {
    override fun initGui() {
        Keyboard.enableRepeatEvents(true)

        +TextField {
            x = 50.0f
            y = 50.0f
            width = 185.0f
            staticText = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore " +
                    "magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, " +
                    "no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam" +
                    " nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo " +
                    "duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet."
            adaptHeight = true
        } id "test"

        GraphicsEngine.runAfter(10_000) {
            getWidget<TextField>("test")!!.x = 100.0f
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawRect(0, 0, width, height, Color(0x1c1f2b).rgb)
        super.drawScreen(mouseX, mouseY, partialTicks)
    }
}