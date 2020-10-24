package net.inceptioncloud.dragonfly.engine.tooltip

import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.internal.Defaults
import net.inceptioncloud.dragonfly.engine.internal.Widget
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseQuad
import net.inceptioncloud.dragonfly.engine.structure.IPosition

/**
 * ## Tooltip
 *
 * Handles the communication between a [TooltipWidget] and its host. This class holds information
 * about the tooltip and handles the positioning and updating.
 *
 * @param text The text to be displayed on the tooltip
 * @param alignment The position of the tooltip based on the [host] widget
 * @param offset The gap between the [host] widget and the tooltip
 */
class Tooltip(
    var text: String,
    var alignment: TooltipAlignment = TooltipAlignment.ABOVE,
    var offset: Double = 10.0
) {
    /**
     * The tooltip widget instance that represents this tooltip
     */
    val widget = TooltipWidget()

    /**
     * The host widget that this tooltip belongs to. This value is set by the widget when its
     * [tooltip][Widget.tooltip] property is set.
     */
    lateinit var host: Widget<*>

    /**
     * Prepares the tooltip for the [host] widget by applying all preferences to the tooltip
     * [widget] and sets its position. Called when the [host] widget's state has changed.
     */
    fun prepare() {
        widget.apply {
            opacity = 0.0F
            stagePriority = 999
            alignment = this@Tooltip.alignment
            text = this@Tooltip.text
        }
        setPosition()
    }

    /**
     * Changes whether the tooltip is [visible] by applying an animation to the tooltip [widget].
     */
    fun animateTooltip(visible: Boolean) {
        widget.detachAnimation<MorphAnimation>()
        widget.morph(
            40, EaseQuad.IN_OUT,
            TooltipWidget::opacity to if (visible) 1.0 else 0.0,
            TooltipWidget::verticalOffset to if (visible) getVerticalOffset() else 0.0
        )?.start()
    }

    /**
     * Updates the text of the tooltip to the [newText].
     */
    fun updateText(newText: String) {
        text = newText
        prepare()
    }

    /**
     * Sets the position of the [widget] based on the [host] and the specified [alignment].
     */
    private fun setPosition() {
        val host = host
        val (width, _) = Defaults.getSizeOrDimension(host)
        if (host !is IPosition) return

        widget.x = host.x + (width / 2)
        widget.y = getVerticalPosition()
    }

    /**
     * Calculates the vertical position based on the [host] and the [alignment].
     */
    private fun getVerticalPosition(): Float {
        val host = host
        val (_, height) = Defaults.getSizeOrDimension(host)
        if (host !is IPosition) error("Host widget must have a position!")

        return when(alignment) {
            TooltipAlignment.ABOVE -> host.y - widget.fontRenderer.height - widget.arrowSize - (widget.padding * 2)
            TooltipAlignment.BELOW -> host.y + height + widget.arrowSize
        }
    }

    /**
     * Returns the vertical offset based on the [alignment].
     */
    private fun getVerticalOffset(): Double = when(alignment) {
        TooltipAlignment.ABOVE -> -offset
        TooltipAlignment.BELOW -> offset
    }
}