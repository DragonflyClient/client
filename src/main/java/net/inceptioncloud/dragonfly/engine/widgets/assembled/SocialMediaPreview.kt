package net.inceptioncloud.dragonfly.engine.widgets.assembled

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseQuad
import net.inceptioncloud.dragonfly.engine.structure.*
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.inceptioncloud.dragonfly.utils.Keep
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ResourceLocation
import java.net.URI

class SocialMediaPreview(
    val network: Network,
    val text: String,
    val url: String,
    initializerBlock: (SocialMediaPreview.() -> Unit)? = null
) : AssembledWidget<SocialMediaPreview>(initializerBlock), IPosition, IDimension, IColor {

    override var x: Double by property(0.0)
    override var y: Double by property(0.0)
    override var width: Double by property(0.0)
    override var height: Double by property(0.0)
    override var color: WidgetColor by property(DragonflyPalette.background)

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "text" to TextField(),
        "image" to Image()
    )

    override fun updateStructure() {
        "image"<Image> {
            x = this@SocialMediaPreview.x
            y = this@SocialMediaPreview.y
            width = this@SocialMediaPreview.height
            height = this@SocialMediaPreview.height
            resourceLocation = network.resourceLocation
            color = WidgetColor(255, 255, 255, 255)
        }

        "text"<TextField> {
            x = this@SocialMediaPreview.x + this@SocialMediaPreview.height + 10.0
            y = this@SocialMediaPreview.y - 2.0
            width = this@SocialMediaPreview.width - this@SocialMediaPreview.height - 10.0
            height = this@SocialMediaPreview.height - 2.0
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = height.toInt(), useScale = false)
            textAlignVertical = Alignment.CENTER
            staticText = text
            color = this@SocialMediaPreview.color
        }
    }

    override fun handleHoverStateUpdate() {
        detachAnimation<MorphAnimation>()
        if (isHovered) {
            morph(50, EaseQuad.IN_OUT, ::color to DragonflyPalette.accentNormal)
        } else {
            morph(50, EaseQuad.IN_OUT, ::color to DragonflyPalette.background)
        }?.start()
    }

    override fun handleMousePress(data: MouseData) {
        if (isHovered) {
            GuiScreen.openWebLink(URI(url))
        }
    }
}

@Keep
enum class Network {
    TWITTER,
    DISCORD,
    INSTAGRAM,
    GITHUB;

    val resourceLocation = ResourceLocation("dragonflyres/icons/socialmedia/${name.toLowerCase()}.png")
}