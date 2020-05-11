package net.inceptioncloud.minecraftmod.engine.structure

import net.minecraft.client.renderer.GlStateManager
import org.apache.logging.log4j.LogManager

/**
 * ## Drawable Interface
 *
 * Every graphics object that can be drawn implements this interface.
 */
interface IDraw
{
    /**
     * Invokes the full render process of the object.
     *
     * Calls the [preRender], [render] and [postRender] functions that the object should implement.
     * In case the rendering process fails, it makes sure that the post-render function is still called.
     */
    fun drawNative()
    {
        preRender()

        try
        {
            render()
        } catch (e: Exception)
        {
            LogManager.getRootLogger().error("An error occurred while rendering drawable!", e)
        }
        finally
        {
            postRender()
        }
    }

    /**
     * Contains the core rendering process of the object.
     *
     * This method must be implemented by the object as it is responsible for drawing the object itself.
     * The process is wrapped between the [preRender] and the [postRender] calls.
     */
    fun render()

    /**
     * Prepares the object to be rendered on the screen.
     *
     * This is the first method that is called during the rendering process. By default, it contains
     * general calls that prepare the rendering, but it can be overwritten to add specific calls for the
     * widget.
     */
    fun preRender()
    {
        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
    }

    /**
     * Finishes the object rendering process.
     *
     * This is the last method that is called during the rendering process.
     */
    fun postRender()
    {
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
    }
}