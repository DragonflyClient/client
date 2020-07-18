package net.inceptioncloud.dragonfly.overlay

import com.google.common.eventbus.Subscribe
import net.inceptioncloud.dragonfly.engine.internal.WidgetBuffer
import net.inceptioncloud.dragonfly.event.client.PostRenderEvent
import net.inceptioncloud.dragonfly.event.client.ResizeEvent

object ScreenOverlay {

    val buffer = WidgetBuffer()

    @Subscribe
    fun onPostRender(event: PostRenderEvent) {
        buffer.render()
    }

    @Subscribe
    fun onResize(event: ResizeEvent) {

    }
}