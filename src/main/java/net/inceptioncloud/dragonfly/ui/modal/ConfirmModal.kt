package net.inceptioncloud.dragonfly.ui.modal

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.widgets.assembled.*
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.inceptioncloud.dragonfly.options.sections.StorageOptions
import net.inceptioncloud.dragonfly.overlay.modal.Modal
import net.inceptioncloud.dragonfly.overlay.modal.ModalWidget
import net.minecraft.util.ResourceLocation

/**
 * Reusable modal window to let the user confirm certain actions.
 *
 * @param title The title for the confirmation modal
 * @param description A description that should explain the consequences of the user's decision
 * @param yesText The text for the button to confirm the action
 * @param noText The text for the button to abandon the action
 * @param icon The icon to be shown on the confirmation modal
 * @param respondImmediately If this value is true, the modal will call the [responder] immediately
 * when a button is pressed. Otherwise, the modal is closed first.
 * @param responder A function that accepts the response of the user
 */
open class ConfirmModal(
    var title: String,
    var description: String,
    var yesText: String = "Yes",
    var noText: String = "No",
    var icon: ResourceLocation = ResourceLocation("dragonflyres/icons/question_emoji.png"),
    private var respondImmediately: Boolean = false,
    private var responder: (Boolean) -> Unit
) : ModalWidget("Confirm", 400.0, 600.0) {

    /**
     * The padding of the container box.
     */
    val padding = 35.0

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "container" to RoundedRectangle(),
        "image" to Image(),
        "title" to TextField(),
        "description" to TextField(),
        "yes-button" to OutlineButton(),
        "no-button" to OutlineButton()
    )

    override fun updateStructure() {
        val image = "image"<Image> {
            width = 96.0
            height = width
            x = this@ConfirmModal.x + (this@ConfirmModal.width - width) / 2
            y = this@ConfirmModal.y + 25.0
            resourceLocation = icon
        }!!

        val title = "title"<TextField> {
            x = this@ConfirmModal.x + this@ConfirmModal.padding
            y = image.y + image.height + 35.0
            width = this@ConfirmModal.width - 2 * this@ConfirmModal.padding
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 60, useScale = false)
            staticText = title
            textAlignHorizontal = Alignment.CENTER
            adaptHeight = true
            adaptHeight()
        }!!

        val description = "description"<TextField> {
            x = this@ConfirmModal.x + this@ConfirmModal.padding
            y = title.y + title.height + 35.0
            width = this@ConfirmModal.width - 2 * this@ConfirmModal.padding
            staticText = description
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 40, useScale = false)
            textAlignHorizontal = Alignment.CENTER
            adaptHeight = true
            adaptHeight()
        }!!

        val yesButton = "yes-button"<OutlineButton> {
            x = this@ConfirmModal.x + this@ConfirmModal.padding
            y = description.y + description.height + 35.0
            width = this@ConfirmModal.width - (padding * 2)
            height = 40.0
            text = yesText
            color = DragonflyPalette.accentNormal
            onClick {
                respond(true)
            }
        }!!

        val noButton = "no-button"<OutlineButton> {
            x = this@ConfirmModal.x + this@ConfirmModal.padding
            y = yesButton.y + yesButton.height + 20.0
            width = this@ConfirmModal.width - (padding * 2)
            height = 40.0
            text = noText
            onClick {
                respond(false)
            }
        }!!

        height = noButton.y + noButton.height - y + padding

        "container"<RoundedRectangle> {
            x = this@ConfirmModal.x
            y = this@ConfirmModal.y
            width = this@ConfirmModal.width
            height = this@ConfirmModal.height
            color = DragonflyPalette.background
            arc = 10.0
        }
    }

    private fun respond(value: Boolean) {
        if (isAnimating) return
        if (respondImmediately) {
            responder(value)
            Modal.hideModal()
        } else {
            Modal.hideModal {
                responder(value)
            }
        }
    }

    override fun handleMousePress(data: MouseData) {
        super.handleMousePress(data)
        structure.values.forEach { it.handleMousePress(data) }
    }
}