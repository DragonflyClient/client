package net.inceptioncloud.minecraftmod.event.client

import net.minecraft.client.renderer.EntityRenderer

/**
 * After the window content has been fully rendered.
 *
 *
 * The Minecraft client and it's graphical user interface is mainly rendered in the
 * [EntityRenderer.updateCameraAndRender] function. After the rendering process
 * has finished, this event will be fired.
 */
data class PostRenderEvent(
    val scaledWidth: Int,
    val scaledHeight: Int,
    val scaledMouseX: Int,
    val scaledMouseY: Int
)
