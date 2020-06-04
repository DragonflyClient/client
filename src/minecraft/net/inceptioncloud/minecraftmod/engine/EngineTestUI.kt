package net.inceptioncloud.minecraftmod.engine

import net.inceptioncloud.minecraftmod.Dragonfly
import net.inceptioncloud.minecraftmod.engine.internal.Alignment
import net.inceptioncloud.minecraftmod.engine.internal.WidgetColor
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
            fontRenderer = Dragonfly.fontDesign.medium,
            widgetColor = Color.WHITE.widget,
            backgroundColor = Color.RED.widget,
            textAlignHorizontal = Alignment.CENTER,
            textAlignVertical = Alignment.CENTER,
            x = 50.0,
            y = 50.0,
            width = 70.0,
            height = 20.0
        ) id "sample-text-glyph"
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawRect(0, 0, width, height, Color(50, 50, 50).rgb)
        super.drawScreen(mouseX, mouseY, partialTicks)
    }
}