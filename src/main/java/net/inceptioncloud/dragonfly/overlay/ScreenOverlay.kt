package net.inceptioncloud.dragonfly.overlay

import com.google.common.eventbus.Subscribe
import net.inceptioncloud.dragonfly.engine.internal.*
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

    @JvmStatic
    fun setColorOverlay(color: WidgetColor) {

    }

    /**
     * Renders the stage on the screen.
     */
    @Subscribe
    fun onPostRender(event: PostRenderEvent) {
        stage.render()
    }
}