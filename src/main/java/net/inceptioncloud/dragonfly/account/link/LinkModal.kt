package net.inceptioncloud.dragonfly.account.link

import kotlinx.coroutines.*
import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.apps.accountmanager.Account
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.widgets.assembled.*
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.inceptioncloud.dragonfly.mc
import net.inceptioncloud.dragonfly.overlay.modal.Modal
import net.inceptioncloud.dragonfly.overlay.modal.ModalWidget
import net.inceptioncloud.dragonfly.overlay.toast.Toast
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.util.ResourceLocation

/**
 * A modal window that is used to ask the user whether he wants to link the given
 * Minecraft [account] to their Dragonfly account.
 */
class LinkModal(
    val account: Account
) : ModalWidget("Link Minecraft", 400.0, 600.0) {

    /**
     * The padding of the container box.
     */
    val padding = 35.0

    /**
     * Whether the modal window is currently in the linking process. If this value is true,
     * the link button cannot be pressed again.
     */
    var isLinking = false

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "container" to RoundRectangle(),
        "image" to Image(),
        "title" to TextField(),
        "info" to TextField(),
        "link-button" to OutlineButton(),
        "dont-link-button" to OutlineButton(),
        "skip-text" to TextField()
    )

    override fun updateStructure() {
        "container"<RoundRectangle> {
            x = this@LinkModal.x
            y = this@LinkModal.y
            width = this@LinkModal.width
            height = this@LinkModal.height
            color = DragonflyPalette.background
            arc = 10.0
        }

        val image = "image"<Image> {
            width = 100.0
            height = width
            x = this@LinkModal.x + (this@LinkModal.width - width) / 2
            y = this@LinkModal.y + 25.0
            resourceLocation = ResourceLocation("dragonflyres/icons/mainmenu/steve-skull.png")
        }!!

        val title = "title"<TextField> {
            x = this@LinkModal.x
            y = image.y + image.height + 10.0
            width = this@LinkModal.width
            height = 60.0
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 60, useScale = false)
            staticText = "Link Minecraft Account"
            textAlignHorizontal = Alignment.CENTER
            textAlignVertical = Alignment.CENTER
        }!!

        val info = "info"<TextField> {
            x = this@LinkModal.x + this@LinkModal.padding
            y = title.y + title.height + 20.0
            width = this@LinkModal.width - 2 * this@LinkModal.padding
            height = 170.0
            staticText = "In order to activate certain features like cosmetics and statistics, " +
                    "you have to link your Minecraft account to your Dragonfly account.\n" +
                    "Do you wish to link §6${account.displayName} §rwith your Dragonfly account §6${Dragonfly.account?.username}§r?"
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 40, useScale = false)
            textAlignHorizontal = Alignment.CENTER
        }!!

        val link = "link-button"<OutlineButton> {
            x = this@LinkModal.x + this@LinkModal.padding
            y = info.y + info.height + 30.0
            width = this@LinkModal.width - (padding * 2)
            height = 40.0
            text = "Link"
            color = DragonflyPalette.accentNormal
            onClick {
                link()
            }
        }!!

        val dontLink = "dont-link-button"<OutlineButton> {
            x = this@LinkModal.x + this@LinkModal.padding
            y = link.y + link.height + 20.0
            width = this@LinkModal.width - (padding * 2)
            height = 40.0
            text = "Don't link"
            onClick {
                Modal.hideModal()
            }
        }!!

        "skip-text"<TextField> {
            x = this@LinkModal.x
            y = dontLink.y + dontLink.height + 25.0
            width = this@LinkModal.width
            height = 40.0
            color = DragonflyPalette.background.brighter(0.5)
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 40, useScale = false)
            staticText = "Don't ask again"
            textAlignHorizontal = Alignment.CENTER
            textAlignVertical = Alignment.CENTER
            clickAction = {
                try {
                    account.getSkipLinkOption().set(true)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                Modal.hideModal()
            }
            hoverAction = {
                if (isHovered) morph(50, null, TextField::color to DragonflyPalette.background.brighter(0.3))?.start()
                else morph(50, null, TextField::color to DragonflyPalette.background.brighter(0.5))?.start()
            }
        }

        GlobalScope.launch {
            val skull = account.retrieveSkull()
            if (skull != null) {
                mc.addScheduledTask {
                    image.dynamicTexture = DynamicTexture(skull)
                }
            }
        }
    }

    /**
     * Performs the login process.
     */
    private fun link() {
        if (isLinking) return

        isLinking = true
        GlobalScope.launch(Dispatchers.IO) {
            try {
                Toast.queue("Linking Minecraft account...", 200)
                LinkBridge.link(account.uuid, account.accessToken)
                Toast.queue("§aAccount successfully linked", 500)
                Modal.hideModal()
            } catch (e: Exception) {
                Toast.queue("§cLinking failed: §r${e.message}", 500)
                isLinking = false
            }
        }
    }
}