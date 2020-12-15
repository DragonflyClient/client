package net.inceptioncloud.dragonfly.engine.widgets.assembled

import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.structure.*
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.util.ResourceLocation

/**
 * ## Responsive Image
 *
 * A wrapper around an image which resizes it to keep its aspect ratio in the container.
 * Acts like `object-fit: cover` in CSS.
 *
 * ### MDN web docs
 * > "The replaced content is sized to maintain its aspect ratio while filling the element’s
 * entire content box. If the object's aspect ratio does not match the aspect ratio of its
 * box, then the object will be clipped to fit."
 *
 * @property originalWidth the original with of the image
 * @property originalHeight the original height of the image
 * @property resourceLocation the resource location of the image
 */
class ResponsiveImage(
    initializerBlock: (ResponsiveImage.() -> Unit)? = null
) : AssembledWidget<ResponsiveImage>(initializerBlock), IPosition, IDimension, IColor {

    override var x: Double by property(0.0)
    override var y: Double by property(0.0)
    override var width: Double by property(50.0)
    override var height: Double by property(50.0)
    override var color: WidgetColor by property(WidgetColor.DEFAULT)

    var originalWidth: Double by property(width)
    var originalHeight: Double by property(width)
    var resourceLocation: ResourceLocation? by property(null)
    var dynamicTexture: DynamicTexture? by property(null)

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
}