package net.inceptioncloud.dragonfly.overlay

import com.google.common.eventbus.Subscribe
import net.inceptioncloud.dragonfly.engine.internal.AssembledWidget
import net.inceptioncloud.dragonfly.engine.internal.WidgetBuffer
import net.inceptioncloud.dragonfly.event.client.PostRenderEvent
import net.inceptioncloud.dragonfly.event.client.ResizeEvent
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import java.awt.Dimension

/**
 * Manages all components that are added to the screen overlay and renders them.
 */
object ScreenOverlay {

    /**
     * A widget buffer that contains all [components][OverlayComponent].
     */
    val buffer = WidgetBuffer()

    /**
     * Getter for conveniently accessing the scaled dimensions of the screen.
     */
    val dimensions: Dimension
        get() {
            val scaledResolution = ScaledResolution(Minecraft.getMinecraft())
            return Dimension(scaledResolution.scaledWidth, scaledResolution.scaledHeight)
        }

    /**
     * Adds a component to the screen [buffer].
     */
    @JvmStatic
    fun addComponent(name: String, component: AssembledWidget<*>) {
        buffer.add(name to component)
    }

    /**
     * Renders the buffer on the screen.
     */
    @Subscribe
    fun onPostRender(event: PostRenderEvent) {
        buffer.render()
    }

    /**
     * Re-initializes the [components][OverlayComponent] in the buffer when the window
     * is resized.
     */
    @Subscribe
    fun onResize(event: ResizeEvent) {
        buffer.content
            .map { it as? AssembledWidget<*> }
            .forEach { it?.reassemble() }
    }
}