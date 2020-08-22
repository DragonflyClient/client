package net.inceptioncloud.dragonfly.overlay.modal

import net.inceptioncloud.dragonfly.apps.accountmanager.AccountCard
import net.inceptioncloud.dragonfly.engine.GraphicsEngine
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.animation.post
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.sequence.easing.*
import net.inceptioncloud.dragonfly.engine.structure.IDimension
import net.inceptioncloud.dragonfly.engine.structure.IPosition
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle
import net.inceptioncloud.dragonfly.overlay.ScreenOverlay
import net.inceptioncloud.dragonfly.overlay.ScreenOverlay.stage
import org.apache.logging.log4j.LogManager

object Modal {

    var currentModal: Widget<*>? = null

    private var inHidingAnimation = false

    fun showModal(modal: Widget<*>): Boolean {
        if (modal !is IPosition || modal !is IDimension) return false
        if (currentModal != null) return false

        LogManager.getLogger().info("Displaying modal: ${modal::class.simpleName}");
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
        modalShadow.morph(75, EaseQuad.OUT, Rectangle::color to WidgetColor(0, 0, 0, 200))?.start()

        ScreenOverlay.addComponent("modal", modal)

        modal.isModal = true
        modal.x = screenWidth / 2.0 - modal.width / 4.0
        modal.y = screenHeight
        modal.scaleFactor = 0.5
        modal.morph(
            100, EaseBack.OUT,
            AccountCard::scaleFactor to 1.0,
            AccountCard::x to screenWidth / 2.0 - modal.width / 2.0,
            AccountCard::y to screenHeight / 2.0 - modal.height / 2.0
        )?.start()

        GraphicsEngine.runAfter(5_000) {
            hideModal()
        }

        return true
    }

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
            modalShadow.morph(75, EaseQuad.IN, Rectangle::color to WidgetColor(0, 0, 0, 0))
                ?.post { _, _ -> stage.remove("modal-shadow") }?.start()
        }

        modal.morph(
            100, EaseBack.IN,
            AccountCard::scaleFactor to 0.5,
            AccountCard::x to screenWidth / 2.0 - modal.width / 4.0,
            AccountCard::y to screenHeight
        )?.post { _, _ ->
            stage.remove("modal")
            currentModal = null
            inHidingAnimation = false
        }?.start()

        return true
    }

    @JvmStatic
    fun isModalPresent() = currentModal != null
}