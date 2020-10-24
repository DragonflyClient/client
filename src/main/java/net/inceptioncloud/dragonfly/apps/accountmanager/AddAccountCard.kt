package net.inceptioncloud.dragonfly.apps.accountmanager

import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.*
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseQuad
import net.inceptioncloud.dragonfly.engine.structure.IDimension
import net.inceptioncloud.dragonfly.engine.structure.IPosition
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle
import net.inceptioncloud.dragonfly.overlay.modal.Modal
import net.minecraft.util.ResourceLocation

class AddAccountCard(
    initializerBlock: (AddAccountCard.() -> Unit)? = null
) : AssembledWidget<AddAccountCard>(initializerBlock), IPosition, IDimension {

    override var x: Float by property(0.0F)
    override var y: Float by property(0.0F)
    override var width: Float = -1.0f
    override var height: Float by property(450.0F)

    val accentColor: WidgetColor = DragonflyPalette.foreground

    var opacity: Float by property(0.2F)

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "container" to Rectangle(),
        "icon" to Image()
    )

    override fun updateStructure() {
        this.width = AccountCard.getCardWidth("") // returns the minimum width

        "container"<Rectangle> {
            x = this@AddAccountCard.x
            y = this@AddAccountCard.y
            width = this@AddAccountCard.width
            height = this@AddAccountCard.height
            color = accentColor.altered { alphaFloat = opacity }
            outlineColor = accentColor
            outlineStroke = 2.0f
        }

        "icon"<Image> {
            resourceLocation = ResourceLocation("dragonflyres/icons/add.png")
            width = 90.0f
            height = 90.0f
            x = this@AddAccountCard.x + this@AddAccountCard.width / 2 - width / 2
            y = this@AddAccountCard.y + this@AddAccountCard.height / 2 - height / 2
        }
    }

    override fun handleHoverStateUpdate() {
        if (isHovered) {
            detachAnimation<MorphAnimation>()
            morph(
                30, EaseQuad.IN_OUT,
                AddAccountCard::opacity to 0.3
            )?.start()
        } else {
            detachAnimation<MorphAnimation>()
            morph(
                30, EaseQuad.IN_OUT,
                AddAccountCard::opacity to 0.2
            )?.start()
        }
    }

    override fun handleMousePress(data: MouseData) {
        if (data in this) {
            Modal.showModal(AddAccountModal())
        }
    }
}