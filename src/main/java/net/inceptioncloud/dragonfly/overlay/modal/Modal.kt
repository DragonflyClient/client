package net.inceptioncloud.dragonfly.overlay.modal

import com.google.common.eventbus.Subscribe
import net.inceptioncloud.dragonfly.engine.GraphicsEngine
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.animation.post
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.sequence.easing.*
import net.inceptioncloud.dragonfly.engine.structure.IDimension
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle
import net.inceptioncloud.dragonfly.event.client.ResizeEvent
import net.inceptioncloud.dragonfly.overlay.ScreenOverlay
import net.inceptioncloud.dragonfly.overlay.ScreenOverlay.stage
import net.inceptioncloud.dragonfly.overlay.hotaction.HotActionWidget
import org.apache.logging.log4j.LogManager
import java.util.concurrent.LinkedBlockingQueue

/**
 * Manages modal windows that are displayed on the screen overlay.
 */
object Modal {

    /**
     * The currently shown modal window or null if none is present.
     */
    var currentModal: ModalWidget? = null

    /**
     * A queue that contains modals that should have shown up while another
     * modal was active.
     */
    private val queue = LinkedBlockingQueue<ModalWidget>()

    /**
     * Whether the modal is currently in its hiding animation and thus cannot
     * be hidden again ([hideModal]).
     */
    private var inHidingAnimation = false

    /**
     * Shows a new [modal] window on the screen.
     *
     * This function uses a smooth popup animation and darkens the background before showing
     * the full [modal] window. Returns true if the widget was successfully shown and false
     * if it was added to the queue.
     */
    fun showModal(modal: ModalWidget): Boolean {
        if (currentModal != null) {
            queue.offer(modal)
            return false
        }

        LogManager.getLogger().info("Displaying modal: ${modal.name}");
        currentModal = modal

        val screenWidth = ScreenOverlay.dimensions.getWidth()
        val screenHeight = ScreenOverlay.dimensions.getHeight()

        val modalShadow = ScreenOverlay.addComponent("modal-shadow", Rectangle().apply {
            x = 0.0
            y = 0.0
            width = screenWidth
            height = screenHeight
            color = WidgetColor(0, 0, 0, 0)
        })
        modalShadow.morph(75, EaseQuad.OUT, Rectangle::color to WidgetColor(0, 0, 0, 180))?.start()

        ScreenOverlay.addComponent("modal", modal)

        modal.isModal = true
        modal.x = screenWidth / 2.0 - modal.width / 4.0
        modal.y = screenHeight
        modal.scaleFactor = 0.5
        modal.onShow()
        modal.detachAnimation<MorphAnimation>()
        modal.morph(
            100, EaseBack.OUT,
            ModalWidget::scaleFactor to 1.0,
            ModalWidget::x to screenWidth / 2.0 - modal.width / 2.0,
            ModalWidget::y to screenHeight / 2.0 - modal.height / 2.0
        )?.start()

        return true
    }

    /**
     * Hides the [currentModal] if one is set. Returns whether a modal window was hidden.
     */
    fun hideModal(): Boolean {
        if (currentModal == null) return false
        if (inHidingAnimation) return false

        inHidingAnimation = true

        val screenWidth = ScreenOverlay.dimensions.getWidth()
        val screenHeight = ScreenOverlay.dimensions.getHeight()

        val modal = stage["modal"]!!
        modal as IDimension
        val modalShadow = stage["modal-shadow"]!!

        GraphicsEngine.runAfter(125) {
            modalShadow.detachAnimation<MorphAnimation>()
            modalShadow.morph(75, EaseQuad.IN, Rectangle::color to WidgetColor(0, 0, 0, 0))
                ?.post { _, _ -> stage.remove("modal-shadow") }?.start()
        }

        modal.detachAnimation<MorphAnimation>()
        modal.morph(
            100, EaseBack.IN,
            ModalWidget::scaleFactor to 0.5,
            ModalWidget::x to screenWidth / 2.0 - modal.width / 4.0,
            ModalWidget::y to screenHeight
        )?.post { _, _ ->
            stage.remove("modal")
            currentModal = null
            inHidingAnimation = false

            if (queue.isNotEmpty()) {
                showModal(queue.poll())
            }
        }?.start()

        return true
    }

    @Subscribe
    fun onResize(event: ResizeEvent) {
        (stage["modal"] as? ModalWidget)?.apply {
            scaleFactor = 1.0
            x = ScreenOverlay.dimensions.getWidth() / 2.0 - width / 2.0
            y = ScreenOverlay.dimensions.getHeight() / 2.0 - height / 2.0
        }
        (stage["modal-shadow"] as? Rectangle)?.apply {
            x = 0.0
            y = 0.0
            width = ScreenOverlay.dimensions.getWidth()
            height = ScreenOverlay.dimensions.getHeight()
            color = WidgetColor(0, 0, 0, 200)
        }
    }

    /**
     * Returns whether a modal window is currently present.
     */
    @JvmStatic
    fun isModalPresent() = currentModal != null
}