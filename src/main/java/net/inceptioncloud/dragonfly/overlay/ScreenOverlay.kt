package net.inceptioncloud.dragonfly.overlay

import com.google.common.eventbus.Subscribe
import net.inceptioncloud.dragonfly.engine.internal.WidgetBuffer
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
    fun addComponent(component: OverlayComponent) {
        buffer.add(component.name to component)
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
        buffer.content.map { it as? OverlayComponent }
            .forEach { it?.initialize(event.width.toDouble(), event.height.toDouble()) }
    }
}