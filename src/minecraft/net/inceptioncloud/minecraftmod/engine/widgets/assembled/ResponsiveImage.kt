package net.inceptioncloud.minecraftmod.engine.widgets.assembled

import net.inceptioncloud.minecraftmod.engine.internal.*
import net.inceptioncloud.minecraftmod.engine.internal.annotations.Interpolate
import net.inceptioncloud.minecraftmod.engine.internal.annotations.State
import net.inceptioncloud.minecraftmod.engine.structure.*
import net.inceptioncloud.minecraftmod.engine.widgets.primitive.Image
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation

class ResponsiveImage(
    @property:Interpolate override var x: Double = 0.0,
    @property:Interpolate override var y: Double = 0.0,
    @property:Interpolate override var width: Double = 50.0,
    @property:Interpolate override var height: Double = 50.0,
    @property:Interpolate override var color: WidgetColor = WidgetColor.DEFAULT,

    @property:Interpolate var originalWidth: Double = width,
    @property:Interpolate var originalHeight: Double = width,
    @property:State var resourceLocation: ResourceLocation? = null
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

            x = -(differenceWidth / 2)
            y = -(differenceHeight / 2)
            width = inOriginalWidth
            height = inOriginalHeight
            color = this@ResponsiveImage.color
            resourceLocation = this@ResponsiveImage.resourceLocation
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