package net.inceptioncloud.minecraftmod.engine

import net.inceptioncloud.minecraftmod.engine.animation.alter.MorphAnimation
import net.inceptioncloud.minecraftmod.engine.font.FontWeight
import net.inceptioncloud.minecraftmod.engine.font.WidgetFont
import net.inceptioncloud.minecraftmod.engine.internal.Alignment
import net.inceptioncloud.minecraftmod.engine.internal.WidgetColor
import net.inceptioncloud.minecraftmod.engine.sequence.easing.EaseBack
import net.inceptioncloud.minecraftmod.engine.widget.assembled.TextField
import net.minecraft.client.gui.GuiScreen
import java.awt.Color
import java.text.SimpleDateFormat
import java.util.*

class EngineTestUI : GuiScreen() {
    private val Color.widget: WidgetColor
        get() = WidgetColor(this)

    override fun initGui() {
        +TextField(
            dynamicText = { "It is ${SimpleDateFormat("HH:mm:ss").format(Date())}" },
            font = WidgetFont("Rubik", "Rubik Light", "Rubik", "Rubik Medium"),
            fontWeight = FontWeight.MEDIUM,
            widgetColor = Color.WHITE.widget,
            backgroundColor = Color.BLUE.widget,
            textAlignHorizontal = Alignment.START,
            textAlignVertical = Alignment.START,
            outlineColor = Color.YELLOW.widget,
            outlineStroke = 1.0,
            x = 50.0,
            y = 50.0,
            width = 80.0,
            height = 30.0,
            padding = 5.0
        ) id "sample-text-glyph"
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawRect(0, 0, width, height, Color(50, 50, 50).rgb)
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        val textField = -"sample-text-glyph" as TextField

        textField.attachAnimation(
            MorphAnimation(
                destination = textField.clone().apply {
                    outlineStroke = 0.5
                    outlineColor = Color.ORANGE.widget
                    backgroundColor = Color.WHITE.widget
                    widgetColor = Color.BLUE.widget
                    width = 100.0
                    height = 25.0
                    padding = 10.0
                },
                easing = EaseBack.IN_OUT,
                duration = 200
            )
        ) { start() }

        super.mouseClicked(mouseX, mouseY, mouseButton)
    }
}