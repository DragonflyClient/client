package net.inceptioncloud.dragonfly.engine

import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morphBetween
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseBack
import net.inceptioncloud.dragonfly.engine.widgets.assembled.InputTextField
import net.inceptioncloud.dragonfly.engine.widgets.primitive.FilledCircle
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Keyboard
import java.awt.Color

class EngineTestUI : GuiScreen() {
    override fun initGui() {
        Keyboard.enableRepeatEvents(true)
        +InputTextField(
            x = 100.0,
            y = 100.0,
            width = 200.0,
            label = "Hotkey Message",
            maxStringLength = 300
        ) id "text-input"

        +FilledCircle(
            x = 30.0,
            y = 30.0,
            size = 20.0,
            color = Color.BLUE.toWidgetColor()
        ) id "circle"
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        getWidget<FilledCircle>("circle")?.morphBetween(easing = EaseBack.IN_OUT, first = {
            x = 15.0
            y = 15.0
            size = 50.0
            color = Color.RED.toWidgetColor()
        }, second = {
            x = 30.0
            y = 30.0
            size = 20.0
            color = Color.BLUE.toWidgetColor()
        })

        super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawRect(0, 0, width, height, Color(255, 255, 255).rgb)
        drawRect(width / 2, 0, width, height, Color(0, 0, 0).rgb)
        super.drawScreen(mouseX, mouseY, partialTicks)
    }
}