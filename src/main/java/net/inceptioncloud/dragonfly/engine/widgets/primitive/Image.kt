package net.inceptioncloud.dragonfly.engine.widgets.primitive

import net.inceptioncloud.dragonfly.engine.GraphicsEngine.popScale
import net.inceptioncloud.dragonfly.engine.GraphicsEngine.pushScale
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morphBetween
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.internal.annotations.Interpolate
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseCubic
import net.inceptioncloud.dragonfly.engine.structure.*
import net.inceptioncloud.dragonfly.engine.widgets.assembled.RoundedRectangle
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
 * @property x the x-location of the top-left-corner of the image
 * @property y the y-location of the top-left-corner of the image
 * @property color a color filter that will be applied to the image
 *
 * @property dynamicTexture a dynamic texture that can be manually set
 * @property resourceLocation the location of the resource that will be drawn (has a lower
 * priority than the [dynamicTexture])
 */
class Image(
    initializerBlock: (Image.() -> Unit)? = null
) : AssembledWidget<Image>(initializerBlock), IPosition, IDimension, IColor {

    @Interpolate override var x: Double by property(0.0)
    @Interpolate override var y: Double by property(0.0)
    @Interpolate override var width: Double by property(50.0)
    @Interpolate override var height: Double by property(50.0)
    @Interpolate override var color: WidgetColor by property(WidgetColor.DEFAULT)

    @Interpolate var scale: Double by property(1.0)
    var dynamicTexture: DynamicTexture? by property(null)
    var resourceLocation: ResourceLocation? by property(null)

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

    override fun preRender() {
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        GlStateManager.color(1F, 1F, 1F, 1F)
    }

    override fun postRender() {
        GlStateManager.disableBlend()
    }

    override fun render() {
        val bound = bindTexture()

        if (bound) {
            getWidget<RoundedRectangle>("placeholder")?.isVisible = false

            glDisable(GL_DEPTH_TEST)
            glEnable(GL_BLEND)
            glDepthMask(false)
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO)

            color.glBindColor()

            pushScale(scale)
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
                    first = listOf(::color to WidgetColor(0xECECEC)),
                    second = listOf(::color to WidgetColor(0xF8F8F8))
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
}