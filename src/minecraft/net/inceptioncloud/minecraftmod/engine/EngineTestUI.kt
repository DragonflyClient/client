package net.inceptioncloud.minecraftmod.engine

import net.inceptioncloud.minecraftmod.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.minecraftmod.engine.sequence.easing.EaseBack
import net.inceptioncloud.minecraftmod.engine.widgets.assembled.InputTextField
import net.inceptioncloud.minecraftmod.engine.widgets.primitive.FilledCircle
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
        getWidget<FilledCircle>("circle")?.morph(easing = EaseBack.IN_OUT) {
            x = 15.0
            y = 15.0
            size = 50.0
            color = Color.RED.toWidgetColor()
        }?.start()

        super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawRect(0, 0, width, height, Color(255, 255, 255).rgb)
        super.drawScreen(mouseX, mouseY, partialTicks)
    }
}