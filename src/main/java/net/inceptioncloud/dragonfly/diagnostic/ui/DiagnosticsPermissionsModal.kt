package net.inceptioncloud.dragonfly.diagnostic.ui

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.widgets.assembled.*
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.inceptioncloud.dragonfly.options.sections.StorageOptions
import net.inceptioncloud.dragonfly.overlay.modal.Modal
import net.inceptioncloud.dragonfly.overlay.modal.ModalWidget
import net.inceptioncloud.dragonfly.engine.font.Typography
import net.inceptioncloud.dragonfly.engine.font.font
import net.minecraft.util.ResourceLocation

/**
 * A modal window that is used to ask the user whether he wants to send diagnostic
 * data to the Dragonfly servers.
 */
class DiagnosticsPermissionsModal : ModalWidget("Diagnostics Permissions", 400.0, 600.0) {

    /**
     * The padding of the container box.
     */
    val padding = 35.0

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "container" to RoundedRectangle(),
        "image" to Image(),
        "title" to TextField(),
        "info" to TextField(),
        "send-button" to OutlineButton(),
        "dont-send-button" to OutlineButton()
    )

    override fun updateStructure() {
        "container"<RoundedRectangle> {
            x = this@DiagnosticsPermissionsModal.x
            y = this@DiagnosticsPermissionsModal.y
            width = this@DiagnosticsPermissionsModal.width
            height = this@DiagnosticsPermissionsModal.height
            color = DragonflyPalette.background
            arc = 10.0
        }

        val image = "image"<Image> {
            width = 96.0
            height = width
            x = this@DiagnosticsPermissionsModal.x + (this@DiagnosticsPermissionsModal.width - width) / 2
            y = this@DiagnosticsPermissionsModal.y + 25.0
            resourceLocation = ResourceLocation("dragonflyres/icons/diagnostics.png")
        }!!

        val title = "title"<TextField> {
            x = this@DiagnosticsPermissionsModal.x
            y = image.y + image.height + 10.0
            width = this@DiagnosticsPermissionsModal.width
            height = 60.0
            fontRenderer = font(Typography.HEADING_2)
            staticText = "Send Diagnostic Data"
            textAlignHorizontal = Alignment.CENTER
            textAlignVertical = Alignment.CENTER
        }!!

        val info = "info"<TextField> {
            x = this@DiagnosticsPermissionsModal.x + this@DiagnosticsPermissionsModal.padding
            y = title.y + title.height + 20.0
            width = this@DiagnosticsPermissionsModal.width - 2 * this@DiagnosticsPermissionsModal.padding
            height = 200.0
            staticText = "To keep your game experience as convenient as possible, Dragonfly collects diagnostic data and sends them to our team. " +
                    "This data includes crash reports and other bug information. No personal data except the Dragonfly and Minecraft account name " +
                    "is collected. Information is not shared with third parties."
            fontRenderer = font(Typography.SMALL)
            textAlignHorizontal = Alignment.CENTER
        }!!

        val sendButton = "send-button"<OutlineButton> {
            x = this@DiagnosticsPermissionsModal.x + this@DiagnosticsPermissionsModal.padding
            y = info.y + info.height + 30.0
            width = this@DiagnosticsPermissionsModal.width - (padding * 2)
            height = 40.0
            text = "Allow"
            color = DragonflyPalette.accentNormal
            onClick {
                StorageOptions.SEND_DIAGNOSTICS.set(1)
                Modal.hideModal()
            }
        }!!

        "dont-send-button"<OutlineButton> {
            x = this@DiagnosticsPermissionsModal.x + this@DiagnosticsPermissionsModal.padding
            y = sendButton.y + sendButton.height + 20.0
            width = this@DiagnosticsPermissionsModal.width - (padding * 2)
            height = 40.0
            text = "Deny"
            onClick {
                StorageOptions.SEND_DIAGNOSTICS.set(-1)
                Modal.hideModal()
            }
        }!!
    }

    override fun handleMousePress(data: MouseData) {
        super.handleMousePress(data)
        structure.values.forEach { it.handleMousePress(data) }
    }
}