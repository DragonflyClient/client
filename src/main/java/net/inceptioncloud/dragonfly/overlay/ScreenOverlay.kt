package net.inceptioncloud.dragonfly.overlay

import com.google.common.eventbus.Subscribe
import net.inceptioncloud.dragonfly.engine.internal.WidgetBuffer
import net.inceptioncloud.dragonfly.event.client.PostRenderEvent

object ScreenOverlay {

    val buffer = WidgetBuffer()

    @Subscribe
    fun onPostRender(event: PostRenderEvent) {
        buffer.render()
    }
}