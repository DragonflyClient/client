package net.inceptioncloud.dragonfly.engine.scrollbar

import net.inceptioncloud.dragonfly.engine.internal.Widget
import net.inceptioncloud.dragonfly.engine.structure.IDimension
import net.inceptioncloud.dragonfly.engine.structure.IPosition
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Mouse
import kotlin.math.absoluteValue

class Scrollbar(
    val guiScreen: GuiScreen,
    val overflowY: Double = 0.0
) {
    private val content = mutableListOf<Widget<*>>()

    private val widget = ScrollbarWidget(this)

    private var isEnabled: Boolean = false

    private var positionToGo: Double = 0.0

    fun update() {
        if (!isEnabled) return
        if (positionToGo == widget.currentY) return

        var distance = (positionToGo - widget.currentY) / 20.0

        if (distance.absoluteValue < 0.1) {
            distance = (positionToGo - widget.currentY)
        }

        widget.currentY += distance
        content.forEach {
            if (it !is IPosition) return@forEach
            it.y -= distance
            it.notifyStateChanged()
        }
    }

    fun handleMouseInput() {
        if (!isEnabled) return

        var wheel = Mouse.getEventDWheel()
        if (wheel != 0) {
            wheel = if (wheel > 0) {
                -1
            } else {
                1
            }

            var distance = wheel * (widget.contentHeight / 20.0)
            if (positionToGo + distance < 0.0) {
                distance = -positionToGo
            } else if (positionToGo + distance > widget.contentHeight - widget.screenHeight) {
                distance = widget.contentHeight - widget.screenHeight - positionToGo
            }
            positionToGo += distance
        }
    }

    fun buildWidget(): ScrollbarWidget = widget.apply {
        visiblePart = 0.0
        verticalPositionPart = 0.0
        progress = 0.0

        contentHeight = calculateContentHeight()
        screenHeight = guiScreen.height.toDouble()
        currentY = 0.0

        height = screenHeight

        isEnabled = contentHeight > screenHeight
        this@Scrollbar.isEnabled = isEnabled
    }

    fun attach(widget: Widget<*>) = content.add(widget)

    fun reset() = content.clear()

    private fun calculateContentHeight(): Double = (content.filter { it is IPosition && it is IDimension }
        .map {
            it as IPosition
            it as IDimension

            it.y + it.height
        }.max() ?: 0.0) + overflowY
}

fun Widget<*>.attachTo(scrollbar: Scrollbar) = scrollbar.attach(this)