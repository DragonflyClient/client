package net.inceptioncloud.dragonfly.overlay

import com.google.common.eventbus.Subscribe
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.event.client.PostRenderEvent
import net.inceptioncloud.dragonfly.event.client.ResizeEvent

/**
 * Manages all components that are added to the screen overlay and renders them.
 */
object ScreenOverlay {

    /**
     * A widget buffer that contains all [components][OverlayComponent].
     */
    private val buffer = WidgetBuffer()

    /**
     * Adds a component to the screen [buffer].
     */
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