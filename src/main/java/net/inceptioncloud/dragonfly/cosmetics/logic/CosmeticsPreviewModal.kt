package net.inceptioncloud.dragonfly.cosmetics.logic

import com.google.gson.JsonObject
import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.apps.cosmetics.CosmeticsUI
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.font.FontWeight
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseQuad
import net.inceptioncloud.dragonfly.engine.switch
import net.inceptioncloud.dragonfly.engine.widgets.assembled.RoundRectangle
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.inceptioncloud.dragonfly.overlay.modal.Modal
import net.inceptioncloud.dragonfly.overlay.modal.ModalWidget
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.texture.DynamicTexture
import java.net.URI

/**
 * A modal window that shows the user that he has unlocked a new cosmetic item.
 *
 * @param item His instance of the cosmetic item
 * @param model The database model of the cosmetic
 * @param dynamicTexture The texture of the cosmetic model that has been downloaded from the cdn
 */
class CosmeticsPreviewModal(
    val item: CosmeticData,
    val model: JsonObject,
    val dynamicTexture: DynamicTexture
) : ModalWidget("Cosmetics Preview", 400.0, 550.0) {

    init {
        stagePriority = 3000
    }

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "background" to RoundRectangle(),
        "image" to Image(),
        "title" to TextField(),
        "subtitle" to TextField(),
        "button" to RoundRectangle(),
        "button-text" to TextField()
    )

    override fun updateStructure() {
        val image = "image"<Image> {
            width = this@CosmeticsPreviewModal.width - 100.0
            height = this@CosmeticsPreviewModal.width - 100.0
            x = this@CosmeticsPreviewModal.x + this@CosmeticsPreviewModal.width / 2 - width / 2
            y = this@CosmeticsPreviewModal.y + this@CosmeticsPreviewModal.width / 2 - width / 2
            dynamicTexture = this@CosmeticsPreviewModal.dynamicTexture
        }!!

        val title = "title"<TextField> {
            width = this@CosmeticsPreviewModal.width - 40.0
            x = this@CosmeticsPreviewModal.x + 20.0
            y = image.y + image.height
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 70, fontWeight = FontWeight.MEDIUM, useScale = false)
            color = DragonflyPalette.foreground
            staticText = "Congratulations!"
            textAlignHorizontal = Alignment.CENTER
            dropShadow = true
            shadowColor = WidgetColor(0, 0, 0, 50)
            shadowDistance = 3.0
            adaptHeight = true
            adaptHeight()
        }!!

        "subtitle"<TextField> {
            width = this@CosmeticsPreviewModal.width - 40.0
            x = this@CosmeticsPreviewModal.x + 20.0
            y = title.y + title.height
            padding = 20.0
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 40, fontWeight = FontWeight.MEDIUM, useScale = false)
            color = DragonflyPalette.foreground.darker(0.8)
            staticText = "You unlocked ${model.get("name").asString}"
            textAlignHorizontal = Alignment.CENTER
            adaptHeight = true
        }

        val button = "button"<RoundRectangle> {
            width = this@CosmeticsPreviewModal.width
            height = 60.0
            y = this@CosmeticsPreviewModal.y + this@CosmeticsPreviewModal.height - height
            x = this@CosmeticsPreviewModal.x
            topLeftArc = 0.0
            topRightArc = 0.0
            bottomLeftArc = 20.0
            bottomRightArc = 20.0
            color = DragonflyPalette.accentNormal
            hoverAction = {
                detachAnimation<MorphAnimation>()
                morph(
                    30, EaseQuad.IN_OUT,
                    ::color to if (it) DragonflyPalette.accentNormal.brighter(0.8) else DragonflyPalette.accentNormal
                )?.start()
            }
            clickAction = {
                Modal.hideModal {
                    if (item.minecraft == null) {
                        GuiScreen.openWebLink(URI(
                            "https://dashboard.playdragonfly.net/cosmetics?utm_source=client&utm_medium=preview&utm_campaign=cosmetics"
                        ))
                    } else {
                        CosmeticsUI().switch()
                    }
                }
            }
        }!!

        "button-text"<TextField> {
            width = button.width
            height = button.height
            x = button.x
            y = button.y
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 55, fontWeight = FontWeight.MEDIUM, useScale = false)
            color = DragonflyPalette.foreground
            staticText = if (item.minecraft == null) "Equip" else "Customize"
            textAlignHorizontal = Alignment.CENTER
            textAlignVertical = Alignment.CENTER
            dropShadow = true
            shadowColor = WidgetColor(0, 0, 0, 80)
            shadowDistance = 2.0
        }

        "background"<RoundRectangle> {
            x = this@CosmeticsPreviewModal.x
            y = this@CosmeticsPreviewModal.y
            width = this@CosmeticsPreviewModal.width
            height = this@CosmeticsPreviewModal.height
            color = DragonflyPalette.background
            arc = 20.0
        }
    }
}