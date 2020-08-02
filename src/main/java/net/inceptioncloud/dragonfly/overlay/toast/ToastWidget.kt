package net.inceptioncloud.dragonfly.overlay.toast

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.internal.annotations.Interpolate
import net.inceptioncloud.dragonfly.engine.internal.annotations.State
import net.inceptioncloud.dragonfly.engine.structure.IDimension
import net.inceptioncloud.dragonfly.engine.structure.IPosition
import net.inceptioncloud.dragonfly.engine.widgets.assembled.RoundedRectangle
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.overlay.ScreenOverlay
import kotlin.properties.Delegates

/**
 * ## Toast Assembled Widget
 *
 * This widget is used to display a toast message at the bottom of the screen. It is created and
 * displayed using the [Toast] object.
 *
 * @param text the text that is displayed
 * @param duration the duration in ticks that the toast message stays on the screen
 */
class ToastWidget(
    @property:State val text: String = "This is a toast message",
    @property:State val duration: Int = 200,

    @property:Interpolate override var x: Double = -1.0,
    @property:Interpolate override var y: Double = -1.0,
    @property:Interpolate override var width: Double = -1.0,
    @property:Interpolate override var height: Double = -1.0
) : AssembledWidget<ToastWidget>(), IPosition, IDimension {

    /**
     * The time of initialization that is used for the timer (set when invoking [updateStructure])
     */
    var initialTime by Delegates.notNull<Long>()

    /**
     * Whether the timer of the widget has already expired.
     */
    var expired = false

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "container" to RoundedRectangle(),
        "text" to TextField()
    )

    override fun updateStructure() {
        initialTime = System.currentTimeMillis()

        val fontRenderer = Dragonfly.fontDesign.defaultFont.fontRenderer(size = 16)

        val textField = updateWidget<TextField>("text") {
            width = (fontRenderer.getStringWidth(text).toDouble() + 4.0).coerceAtMost(ScreenOverlay.dimensions.width / 2.0)
            x = (ScreenOverlay.dimensions.width) / 2 - (width / 2)
            y = this@ToastWidget.y + 3.0
            staticText = text
            adaptHeight = true
            color = DragonflyPalette.foreground
            textAlignHorizontal = Alignment.CENTER
            this.fontRenderer = fontRenderer
        }!!.also { it.adaptHeight() }

        val container = updateWidget<RoundedRectangle>("container") {
            val padding = 4.0
            arc = 4.0
            x = textField.x - padding
            y = textField.y - padding + 1.0
            width = textField.width + (padding * 2)
            height = textField.height + (padding * 2) - 1.0
            color = DragonflyPalette.background.altered { alphaDouble = 0.8 }
        }!!

        this.x = container.x
        this.y = container.y
        this.width = container.width
        this.height = container.height
    }

    override fun update() {
        val remaining = 1.0 - ((System.currentTimeMillis() - initialTime) / (duration * 5.0)).coerceIn(0.0, 1.0)

        if (remaining == 0.0 && !expired) {
            Toast.finish(this@ToastWidget)
        }

        super.update()
    }

    override fun clone(): ToastWidget = ToastWidget(text, duration, x, y, width, height)

    override fun newInstance(): ToastWidget = ToastWidget()
}
