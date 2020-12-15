package net.inceptioncloud.dragonfly.controls

import net.inceptioncloud.dragonfly.engine.scrollbar.Scrollbar
import net.inceptioncloud.dragonfly.engine.scrollbar.attachTo
import net.minecraft.client.gui.GuiScreen

/**
 * Manages the controls elements for a controls gui.
 *
 * @param guiScreen The screen to which the widgets are added
 * @param originY The start y-coordinate for the control widgets
 * @param originX The x-coordinate for the control widgets
 * @param width The width for the control widgets
 * @param margin The margin between two control widgets
 */
class ControlsManager(
    val guiScreen: GuiScreen,
    var originX: Double = 0.0,
    var originY: Double = 0.0,
    var overflowY: Double = 0.0,
    var width: Double = 0.0,
    var margin: Double = 0.0,
    var scrollbarX: Double? = null
) {
    /**
     * Scrollbar to which the controls are attached.
     */
    var scrollbar = Scrollbar(guiScreen, overflowY)

    /**
     * List of control elements that have been shown via the [show] function.
     */
    private val shownElements = mutableListOf<ControlElement<*>>()

    /**
     * The stage of the [guiScreen] to which the widgets are added.
     */
    private val stage = guiScreen.stage

    /**
     * Shows the given [controls] on the screen. It is recommended to call [reset]
     * before invoking this function since it starts at the [originY] coordinate.
     */
    fun show(controls: Collection<ControlElement<*>>) {
        shownElements.addAll(controls)
        scrollbar = Scrollbar(guiScreen, overflowY)

        var currentY = originY

        for ((index, control) in controls.withIndex()) {
            if (control is TitleControl && index != 0) currentY += margin

            control.x = originX
            control.y = currentY
            control.width = width

            stage.add("control-element-$index" to control)
            scrollbar.let { control.attachTo(it) }

            currentY += control.height + margin
        }

        stage.add("scrollbar" to scrollbar.prepareWidget().apply {
            width = 7.0
            x = (scrollbarX ?: guiScreen.width.toDouble()) - width
            y = 0.0
        })
    }

    /**
     * Resets the whole control manager including the [scrollbar] and all [shownElements].
     */
    fun reset() {
        scrollbar.reset()
        stage.remove("scrollbar")

        shownElements.forEach {
            (it as? OptionControlElement<*>)?.removeListener()
            stage.remove(it)
        }
        shownElements.clear()
    }
}