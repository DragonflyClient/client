package net.inceptioncloud.minecraftmod.engine

import net.inceptioncloud.minecraftmod.engine.animation.alter.MorphAnimation
import net.inceptioncloud.minecraftmod.engine.internal.Alignment
import net.inceptioncloud.minecraftmod.engine.internal.WidgetColor
import net.inceptioncloud.minecraftmod.engine.sequence.easing.EaseQuad
import net.inceptioncloud.minecraftmod.engine.widget.assembled.RoundedRectangle
import net.minecraft.client.gui.GuiScreen
import java.awt.Color

class EngineTestUI : GuiScreen() {
    val Color.widget: WidgetColor
        get() = WidgetColor(this)

    private lateinit var rectangleGreen: RoundedRectangle

    private lateinit var rectangleBlue: RoundedRectangle

    override fun initGui() {
        rectangleGreen = RoundedRectangle(
            x = this@EngineTestUI.width / 2.0,
            y = this@EngineTestUI.height - 20.0,
            width = 5.0,
            height = 5.0,
            arc = 2.5,
            widgetColor = WidgetColor(0x26de81),
            horizontalAlignment = Alignment.CENTER,
            verticalAlignment = Alignment.START
        )
        rectangleBlue = RoundedRectangle(
            x = this@EngineTestUI.width / 2.0,
            y = this@EngineTestUI.height / 2.0,
            width = 100.0,
            height = 100.0,
            arc = 1.0,
            widgetColor = WidgetColor(0x45aaf2),
            horizontalAlignment = Alignment.CENTER,
            verticalAlignment = Alignment.CENTER
        )
        +rectangleGreen.clone() id "morph-rectangle"
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawRect(0, 0, width, height, Color(50, 50, 50).rgb)
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {

        val widget = (-"morph-rectangle") as? RoundedRectangle

        if (widget != null) {
            when {
                widget.isStateEqual(rectangleGreen) -> {
                    println("Widget is Green")
                    widget.attachAnimation(MorphAnimation(rectangleBlue, 60, EaseQuad.IN_OUT)) { start() }
                }
                widget.isStateEqual(rectangleBlue) -> {
                    println("Widget is Blue")
                    widget.attachAnimation(MorphAnimation(rectangleGreen, 60, EaseQuad.IN_OUT)) { start() }
                }
                else -> println("Widget isn't ready!")
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton)
    }
}