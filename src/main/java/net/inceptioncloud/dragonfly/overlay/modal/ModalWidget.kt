package net.inceptioncloud.dragonfly.overlay.modal

import net.inceptioncloud.dragonfly.engine.contains
import net.inceptioncloud.dragonfly.engine.internal.AssembledWidget
import net.inceptioncloud.dragonfly.engine.internal.MouseData
import net.inceptioncloud.dragonfly.engine.structure.IDimension
import net.inceptioncloud.dragonfly.engine.structure.IPosition
import org.lwjgl.input.Keyboard

/**
 * Specification of the [AssembledWidget] class to be used as a modal window on the
 * screen overlay.
 *
 * @param name the name of the modal window
 * @param desiredWidth the width of the window
 * @param desiredHeight the height of the window
 */
abstract class ModalWidget(
    val name: String, desiredWidth: Double, desiredHeight: Double
): AssembledWidget<ModalWidget>(), IDimension, IPosition {

    override var width: Double by property(desiredWidth)
    override var height: Double by property(desiredHeight)
    override var x: Double by property(0.0)
    override var y: Double by property(0.0)

    /**
     * True, if the widget is currently in its animation process in which it should
     * not respond to user input.
     */
    var isAnimating = false

    /**
     * Called when the modal window is shown using [Modal.showModal].
     */
    open fun onShow() {}

    override fun handleKeyTyped(char: Char, keyCode: Int) {
        super.handleKeyTyped(char, keyCode)

        if (keyCode == Keyboard.KEY_ESCAPE) Modal.hideModal()
    }

    override fun handleMousePress(data: MouseData) {
        super.handleMousePress(data)

        if (!isAnimating && data !in this) {
            Modal.hideModal()
        }
    }
}