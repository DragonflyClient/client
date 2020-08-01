package net.inceptioncloud.dragonfly.overlay

import com.google.common.eventbus.Subscribe
import net.inceptioncloud.dragonfly.engine.internal.AssembledWidget
import net.inceptioncloud.dragonfly.engine.internal.WidgetBuffer
import net.inceptioncloud.dragonfly.event.client.PostRenderEvent
import net.inceptioncloud.dragonfly.event.control.KeyStateChangeEvent
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import org.lwjgl.input.Keyboard
import java.awt.Dimension

/**
 * Manages all components that are added to the screen overlay and renders them.
 */
object IngameOverlay {

    /**
     * A widget buffer that contains all components.
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
    fun addComponent(name: String, component: AssembledWidget<*>): AssembledWidget<*> {
        buffer.add(name to component)
        return component
    }

    /**
     * Renders the buffer on the screen.
     */
    @Subscribe
    fun onPostRender(event: PostRenderEvent) {
        if (Minecraft.getMinecraft().currentScreen == null) {
            buffer.render()
        }
    }
}