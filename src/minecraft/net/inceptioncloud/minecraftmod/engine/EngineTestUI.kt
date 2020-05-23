package net.inceptioncloud.minecraftmod.engine

import net.inceptioncloud.minecraftmod.engine.animation.`in`.FloatAnimationIn
import net.inceptioncloud.minecraftmod.engine.animation.alter.MorphAnimation
import net.inceptioncloud.minecraftmod.engine.internal.Alignment
import net.inceptioncloud.minecraftmod.engine.internal.WidgetColor
import net.inceptioncloud.minecraftmod.engine.sequence.easing.EaseQuad
import net.inceptioncloud.minecraftmod.engine.widget.primitive.Rectangle
import net.minecraft.client.gui.GuiScreen
import java.awt.Color

class EngineTestUI : GuiScreen() {
    val Color.widget: WidgetColor
        get() = WidgetColor(this)

    private lateinit var rectangleGreen: Rectangle

    private lateinit var rectangleBlue: Rectangle

    override fun initGui() {
        rectangleGreen = Rectangle(
            x = this@EngineTestUI.width / 2.0,
            y = this@EngineTestUI.height - 1.0,
            width = 40.0,
            height = 2.0,
            widgetColor = WidgetColor(0x4b7bec),
            outlineColor = WidgetColor(0x4b7bec),
            horizontalAlignment = Alignment.CENTER,
            verticalAlignment = Alignment.CENTER
        )
        rectangleBlue = Rectangle(
            x = this@EngineTestUI.width / 2.0,
            y = this@EngineTestUI.height / 2.0,
            width = 150.0,
            height = 170.0,
            widgetColor = WidgetColor(0x45aaf2),
            outlineStroke = 2.0,
            outlineColor = WidgetColor(0x4b7bec),
            horizontalAlignment = Alignment.CENTER,
            verticalAlignment = Alignment.CENTER
        )
        +rectangleGreen.clone().attachAnimation(FloatAnimationIn(100, distance = 5.0)) {
            start()
        } id "morph-rectangle"
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawRect(0, 0, width, height, Color(50, 50, 50).rgb)
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {

        val widget = (-"morph-rectangle") as? Rectangle

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