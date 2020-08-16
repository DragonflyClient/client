package net.inceptioncloud.dragonfly.overlay

import com.google.common.eventbus.Subscribe
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.animation.post
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseQuad
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle
import net.inceptioncloud.dragonfly.event.client.PostRenderEvent
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import java.awt.Dimension

/**
 * Manages all components that are added to the screen overlay and renders them.
 */
object ScreenOverlay {

    /**
     * A widget stage that contains all components.
     */
    val stage = WidgetStage("Screen Overlay")

    /**
     * Getter for conveniently accessing the scaled dimensions of the screen.
     */
    val dimensions: Dimension
        get() {
            val scaledResolution = ScaledResolution(Minecraft.getMinecraft())
            return Dimension(scaledResolution.scaledWidth, scaledResolution.scaledHeight)
        }

    /**
     * Adds a component to the screen [stage].
     */
    @JvmStatic
    fun addComponent(name: String, component: AssembledWidget<*>): AssembledWidget<*> {
        stage.add(name to component)
        return component
    }

    /**
     * Performs the given [action] (mostly a gui change) with a switch overlay while
     * calling it in the moment when the screen is fully covered.
     */
    @JvmStatic
    fun withSwitchOverlay(
        primary: WidgetColor = DragonflyPalette.accentNormal,
        secondary: WidgetColor = DragonflyPalette.background,
        action: () -> Unit
    ) {
        val overlay = Rectangle().apply {
            stagePriority = -100
            x = 0.0
            y = 0.0
            width = 0.0
            height = dimensions.height.toDouble()
            color = primary
        }

        val tail = Rectangle().apply {
            stagePriority = -101
            x = 0.0
            y = 0.0
            width = 0.0
            height = dimensions.height.toDouble()
            color = secondary
        }

        stage.add("switch-overlay" to overlay)
        stage.add("switch-overlay-tail" to tail)

        tail.morph(75, EaseQuad.IN, Rectangle::width to dimensions.width.toDouble())?.start()
        overlay.morph(90, EaseQuad.IN, Rectangle::width to dimensions.width.toDouble())?.post { _, _ ->
            action()
            finishSwitchOverlay()
        }?.start()
    }

    /**
     * Animates the switch overlay added by [withSwitchOverlay] out.
     */
    private fun finishSwitchOverlay() {
        val overlay = stage["switch-overlay"] ?: return
        val tail = stage["switch-overlay-tail"] ?: return

        overlay.detachAnimation<MorphAnimation>()
        tail.detachAnimation<MorphAnimation>()

        overlay.morph(75, EaseQuad.OUT, Rectangle::x to dimensions.width.toDouble())?.start()
        tail.morph(90, EaseQuad.OUT, Rectangle::x to dimensions.width.toDouble())?.post { _, _ ->
            stage.remove("switch-overlay")
            stage.remove("switch-overlay-tail")
        }?.start()
    }

    /**
     * Renders the stage on the screen.
     */
    @Subscribe
    fun onPostRender(event: PostRenderEvent) {
        stage.render()
    }
}