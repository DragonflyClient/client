package net.inceptioncloud.dragonfly.overlay.toast

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.internal.*
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
 * @property text the text that is displayed
 * @property duration the duration in ticks that the toast message stays on the screen
 */
class ToastWidget(
    initializerBlock: (ToastWidget.() -> Unit)? = null
) : AssembledWidget<ToastWidget>(initializerBlock), IPosition, IDimension {

    var text: String by property("This is a toast message")
    var duration: Int by property(200)
    var opacity: Double by property(0.0)

    override var x: Double by property(-1.0)
    override var y: Double by property(-1.0)
    override var width: Double by property(-1.0)
    override var height: Double by property(-1.0)

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

        val fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 40, useScale = false)

        val textField = updateWidget<TextField>("text") {
            width = (fontRenderer.getStringWidth(text).toDouble() + 8.0).coerceAtMost(ScreenOverlay.dimensions.width / 2.0)
            x = (ScreenOverlay.dimensions.width) / 2 - (width / 2)
            y = this@ToastWidget.y + 6.0
            staticText = text
            adaptHeight = true
            color = DragonflyPalette.foreground.altered { alphaDouble = opacity }
            textAlignHorizontal = Alignment.CENTER
            this.fontRenderer = fontRenderer
        }!!.also { it.adaptHeight() }

        val container = updateWidget<RoundedRectangle>("container") {
            val padding = 8.0
            arc = 8.0
            x = textField.x - padding
            y = textField.y - padding + 2.0
            width = textField.width + (padding * 2)
            height = textField.height + (padding * 2) - 2.0
            color = DragonflyPalette.background.altered { alphaDouble = 0.8 * opacity }
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
}