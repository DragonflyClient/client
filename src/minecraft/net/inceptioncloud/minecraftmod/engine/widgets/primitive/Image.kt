package net.inceptioncloud.minecraftmod.engine.widgets.primitive

import net.inceptioncloud.minecraftmod.engine.GraphicsEngine.popScale
import net.inceptioncloud.minecraftmod.engine.GraphicsEngine.pushScale
import net.inceptioncloud.minecraftmod.engine.animation.alter.MorphAnimation.Companion.morphBetween
import net.inceptioncloud.minecraftmod.engine.internal.*
import net.inceptioncloud.minecraftmod.engine.internal.annotations.Info
import net.inceptioncloud.minecraftmod.engine.internal.annotations.Interpolate
import net.inceptioncloud.minecraftmod.engine.sequence.easing.EaseCubic
import net.inceptioncloud.minecraftmod.engine.structure.*
import net.inceptioncloud.minecraftmod.engine.widgets.assembled.RoundedRectangle
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui.drawModalRectWithCustomSizedTexture
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper.glBlendFunc
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11.*
import java.awt.image.BufferedImage

/**
 * ## Image Widget
 *
 * This widget draws an image at the given position with the dimensions that were set.
 *
 * @param x the x-location of the top-left-corner of the image
 * @param y the y-location of the top-left-corner of the image
 * @param width the width of the image
 * @param height the height of the image
 * @param color a color filter that will be applied to the image
 *
 * @param image a buffered image that will be converted to the [dynamicTexture] on set
 * @param dynamicTexture a dynamic texture that can be manually set or that will be set
 * when via the [image] setter
 * @param resourceLocation the location of the resource that will be drawn (has a lower
 * priority than the [dynamicTexture])
 */
class Image(
    @property:Interpolate override var x: Double = 0.0,
    @property:Interpolate override var y: Double = 0.0,
    @property:Interpolate override var width: Double = -1.0,
    @property:Interpolate override var height: Double = -1.0,
    @property:Interpolate override var color: WidgetColor = WidgetColor.DEFAULT,

    @property:Interpolate var scale: Double = 1.0,

    image: BufferedImage? = null,
    @property:Info var dynamicTexture: DynamicTexture? = null,
    @property:Info var resourceLocation: ResourceLocation? = null
) : AssembledWidget<Image>(), IPosition, IDimension, IColor {

    /**
     * The property that corresponds to the `image` parameter in the constructor.
     */
    @Info
    var image: BufferedImage? = image
        set(value) {
            if (value != null) {
                dynamicTexture = DynamicTexture(value)
            }

            field = value
        }

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "placeholder" to RoundedRectangle()
    )

    override fun updateStructure() {
        updateWidget<RoundedRectangle>("placeholder") {
            arc = 3.0
            x = this@Image.x
            y = this@Image.y
            width = this@Image.width
            height = this@Image.height
            color = WidgetColor(0xDFE4EA)
        }
    }

    override fun preRender() {}

    override fun postRender() {}

    override fun render() {
        val bound = bindTexture()

        if (bound) {
            getWidget<RoundedRectangle>("placeholder")?.isVisible = false

            glDisable(GL_DEPTH_TEST)
            glEnable(GL_BLEND)
            glDepthMask(false)
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO)

            color.glBindColor()

            pushScale(scale to scale)
            drawModalRectWithCustomSizedTexture(
                (x / scale).toInt(), (y / scale).toInt(), 0f, 0f,
                (width / scale).toInt(), (height / scale).toInt(), (width / scale).toFloat(), (height / scale).toFloat()
            )
            popScale()

            glDepthMask(true)
            glDisable(GL_BLEND)
            glEnable(GL_DEPTH_TEST)
        } else {
            // draw the placeholder
            getWidget<RoundedRectangle>("placeholder")?.run {
                isVisible = true
                morphBetween(
                    duration = 200,
                    easing = EaseCubic.IN_OUT,
                    first = { color = WidgetColor(0xDFE4EA) },
                    second = { color = WidgetColor(0xCED6E0) }
                )
            }
        }

        super.render()
    }

    /**
     * Binds the texture for the image. At first tries to bind the [dynamicTexture], than the
     * [resourceLocation]. If one of both was set, the function will return true, otherwise false.
     */
    private fun bindTexture(): Boolean {
        when {
            dynamicTexture != null -> GlStateManager.bindTexture(dynamicTexture!!.glTextureId)
            resourceLocation != null -> Minecraft.getMinecraft().textureManager.bindTexture(resourceLocation)
            else -> return false
        }
        return true
    }

    override fun clone(): Image = Image(
        x, y, width, height, color, scale, image, dynamicTexture, resourceLocation
    )

    override fun newInstance(): Image = Image()
}