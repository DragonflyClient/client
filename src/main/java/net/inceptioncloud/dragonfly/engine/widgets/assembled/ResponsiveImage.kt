package net.inceptioncloud.dragonfly.engine.widgets.assembled

import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.internal.annotations.Interpolate
import net.inceptioncloud.dragonfly.engine.internal.annotations.State
import net.inceptioncloud.dragonfly.engine.structure.*
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.util.ResourceLocation

class ResponsiveImage(
    @property:Interpolate override var x: Double = 0.0,
    @property:Interpolate override var y: Double = 0.0,
    @property:Interpolate override var width: Double = 50.0,
    @property:Interpolate override var height: Double = 50.0,
    @property:Interpolate override var color: WidgetColor = WidgetColor.DEFAULT,

    @property:Interpolate var originalWidth: Double = width,
    @property:Interpolate var originalHeight: Double = width,
    @property:State var resourceLocation: ResourceLocation? = null,
    @property:State var dynamicTexture: DynamicTexture? = null
) : AssembledWidget<ResponsiveImage>(), IPosition, IDimension, IColor {

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "image" to Image()
    )

    override fun updateStructure() {
        updateWidget<Image>("image") {
            var inOriginalWidth = originalWidth
            var inOriginalHeight = originalHeight
            val factorWidth = inOriginalWidth / this@ResponsiveImage.width
            val factorHeight = inOriginalHeight / this@ResponsiveImage.height
            val factor = factorWidth.coerceAtMost(factorHeight)

            inOriginalWidth /= factor
            inOriginalHeight /= factor

            val differenceWidth = inOriginalWidth - this@ResponsiveImage.width
            val differenceHeight = inOriginalHeight - this@ResponsiveImage.height

            x = this@ResponsiveImage.x - (differenceWidth / 2)
            y = this@ResponsiveImage.y - (differenceHeight / 2)
            width = inOriginalWidth
            height = inOriginalHeight
            color = this@ResponsiveImage.color
            resourceLocation = this@ResponsiveImage.resourceLocation
            dynamicTexture = this@ResponsiveImage.dynamicTexture
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

    override fun clone(): ResponsiveImage = ResponsiveImage(
        x, y, width, height, color, originalWidth, originalHeight, resourceLocation
    )

    override fun newInstance(): ResponsiveImage = ResponsiveImage()
}
