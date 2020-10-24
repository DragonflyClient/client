package net.inceptioncloud.dragonfly.engine.scrollbar

import net.inceptioncloud.dragonfly.engine.internal.Widget
import net.inceptioncloud.dragonfly.engine.structure.IDimension
import net.inceptioncloud.dragonfly.engine.structure.IPosition
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Mouse
import kotlin.math.absoluteValue

/**
 * Utility class for allowing to scroll through some widgets of the screen, named
 * the [content] of the scrollbar.
 *
 * ## How To
 * Enable scrolling by creating an instance of this class in the [GuiScreen.initGui]
 * function passing all required constructor arguments. To attach widgets to the
 * scrollbar use the [attach] function of the scrollbar or the desired [extension function]
 * [attachTo] for widgets. After all the content has been added, [prepare the scrollbar widget]
 * [prepareWidget] and add it to your screen. If the content of your stage can change
 * without re-entering the gui, make sure to [clear] the scrollbar before re-adding the
 * widgets to avoid duplicates. In order for mouse wheel scrolling to work, call the
 * [handleMouseInput] in the [GuiScreen.handleMouseInput] function.
 *
 * @param guiScreen The screen which this scrollbar belongs to
 * @param overflowY The vertical overflow at the bottom of the content (amount that can be
 * scrolled even if the actual content height is reached)
 */
class Scrollbar(
    val guiScreen: GuiScreen,
    val overflowY: Float = 0.0f
) {

    /**
     * All widgets that are attached to this scrollbar instance.
     */
    val content = mutableListOf<Widget<*>>()

    /**
     * The scrollbar widget created for this scrollbar.
     */
    private val scrollbarWidget = ScrollbarWidget(this)

    /**
     * Whether the scrollbar is enabled. This property is updated in the [prepareWidget]
     * function. If the content height doesn't exceed the screen height, the scrollbar
     * is disabled and thus the [scrollbarWidget] is not visible.
     */
    private var isEnabled: Boolean = false

    /**
     * In order to provide smooth scrolling, this property holds the y position to which the
     * scrollbar is currently scrolling. When receiving mouse wheel input, this property is
     * changed instead of directly scrolling the given distance. The [update] function uses
     * it to scroll the required amount until the current position closely matches this value.
     */
    private var positionToGo: Float = 0.0f

    /**
     * Called by the [scrollbarWidget] whenever it is updated (usually every Dragonfly tick).
     * This function's only purpose is to smoothly animate the scrolling until it reaches the
     * [positionToGo].
     */
    fun update() {
        if (!isEnabled) return
        if (positionToGo == scrollbarWidget.currentY) return

        var distance = (positionToGo - scrollbarWidget.currentY) / 20.0f

        if (distance.absoluteValue < 0.1) {
            distance = (positionToGo - scrollbarWidget.currentY)
        }

        scrollbarWidget.currentY += distance
        content.forEach {
            if (it !is IPosition) return@forEach
            it.y -= distance
            it.notifyStateChanged()
        }
    }

    /**
     * Handles mouse wheel input for the scrollbar and updates the [positionToGo] accordingly
     * to smoothly scroll the desired amount.
     */
    fun handleMouseInput() {
        if (!isEnabled) return

        var wheel = Mouse.getEventDWheel()
        if (wheel != 0) {
            wheel = if (wheel > 0) {
                -1
            } else {
                1
            }

            var distance = wheel * (scrollbarWidget.contentHeight / 20.0f)
            if (positionToGo + distance < 0.0) {
                distance = -positionToGo
            } else if (positionToGo + distance > scrollbarWidget.contentHeight - scrollbarWidget.screenHeight) {
                distance = scrollbarWidget.contentHeight - scrollbarWidget.screenHeight - positionToGo
            }
            positionToGo += distance
        }
    }

    /**
     * Prepares teh [scrollbarWidget] (and the whole scrollbar) by resetting all values that
     * could have changed and [calculate the content height][calculateContentHeight]. This is the
     * only way to access the [scrollbarWidget] since this reset process should always be executed
     * before the scrollbar is used.
     */
    fun prepareWidget(): ScrollbarWidget = scrollbarWidget.apply {
        positionToGo = 0.0f

        visiblePart = 0.0f
        verticalPositionPart = 0.0f
        progress = 0.0f

        contentHeight = calculateContentHeight()
        screenHeight = guiScreen.height.toFloat()
        currentY = 0.0f

        height = screenHeight

        isEnabled = contentHeight > screenHeight
        this@Scrollbar.isEnabled = isEnabled
    }

    /**
     * Attaches the given [widget] to the scrollbar by adding it to the [content].
     */
    fun attach(widget: Widget<*>) = content.add(widget)

    /**
     * Resets the scrollbar by clearing its [content].
     */
    fun reset() = content.clear()

    /**
     * Calculates the content height based on the [content] widget. This function does not
     * respect an other start y-position than 0.0. It searches for the widget whose bottom
     * coordinate is the greatest and returns that coordinate.
     */
    private fun calculateContentHeight(): Float = (content.filter { it is IPosition && it is IDimension }
        .map {
            it as IPosition
            it as IDimension

            it.y + it.height
        }.max() ?: 0.0F) + overflowY
}

/**
 * Extension function for adding the widget to the given [scrollbar].
 */
fun Widget<*>.attachTo(scrollbar: Scrollbar) = scrollbar.attach(this)