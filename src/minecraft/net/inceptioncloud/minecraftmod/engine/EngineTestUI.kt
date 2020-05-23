package net.inceptioncloud.minecraftmod.engine

import net.inceptioncloud.minecraftmod.design.color.BluePalette
import net.inceptioncloud.minecraftmod.engine.animation.`in`.FloatAnimationIn
import net.inceptioncloud.minecraftmod.engine.internal.Alignment
import net.inceptioncloud.minecraftmod.engine.internal.WidgetColor
import net.inceptioncloud.minecraftmod.engine.sequence.easing.EaseBack
import net.inceptioncloud.minecraftmod.engine.widget.assembled.RoundedRectangle
import net.inceptioncloud.minecraftmod.engine.widget.primitive.Arc
import net.inceptioncloud.minecraftmod.engine.widget.primitive.FilledCircle
import net.inceptioncloud.minecraftmod.engine.widget.primitive.Line
import net.minecraft.client.gui.GuiScreen
import java.awt.Color

class EngineTestUI : GuiScreen() {
    val Color.widget: WidgetColor
        get() = WidgetColor(this)

    override fun initGui() {
        +Line(
            endX = 50.0,
            endY = 50.0,
            x = 10.0,
            y = 10.0,
            widgetColor = Color.RED.widget,
            lineWidth = 1.0
        ) id "line"

        +RoundedRectangle(
            x = this@EngineTestUI.width / 2.0,
            y = this@EngineTestUI.height / 2.0,
            width = 200.0,
            height = 150.0,
            widgetColor = WidgetColor(BluePalette.PRIMARY),
            horizontalAlignment = Alignment.CENTER,
            verticalAlignment = Alignment.CENTER,
            arc = 20.0
        ).attachAnimation(FloatAnimationIn(150, 30.0, EaseBack.IN_OUT)) {
            start()
            attach()
        } id "rounded-rectangle"

        +FilledCircle(
            x = this@EngineTestUI.width / 2.0,
            y = this@EngineTestUI.height / 2.0,
            horizontalAlignment = Alignment.CENTER,
            verticalAlignment = Alignment.CENTER,
            size = 50.0,
            widgetColor = WidgetColor(BluePalette.FOREGROUND)
        ) id "center-circle"

        +Arc(
            x = 100.0,
            y = 100.0,
            width = 30.0,
            height = 30.0,
            start = 60,
            end = 200,
            widgetColor = WidgetColor(BluePalette.PRIMARY_DARK)
        ) id "arc"
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawRect(0, 0, width, height, BluePalette.BACKGROUND.rgb)
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

}