package net.inceptioncloud.dragonfly.event.client

import net.inceptioncloud.dragonfly.event.Event
import net.minecraft.client.renderer.EntityRenderer

/**
 * After the window content has been fully rendered.
 *
 * The Minecraft client and it's graphical user interface is mainly rendered in the
 * [EntityRenderer.updateCameraAndRender] function. After the rendering process
 * has finished, this event will be fired.
 */
data class PostRenderEvent(
    val scaledWidth: Double,
    val scaledHeight: Double,
    val scaledMouseX: Double,
    val scaledMouseY: Double
) : Event
