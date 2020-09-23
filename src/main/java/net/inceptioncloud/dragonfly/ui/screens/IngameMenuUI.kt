package net.inceptioncloud.dragonfly.ui.screens

import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.runBlocking
import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.account.LoginStatusWidget
import net.inceptioncloud.dragonfly.apps.accountmanager.AccountManagerApp
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseQuad
import net.inceptioncloud.dragonfly.engine.widgets.assembled.DragonflyButton
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle
import net.inceptioncloud.dragonfly.overlay.modal.Modal
import net.inceptioncloud.dragonfly.ui.modal.ConfirmModal
import net.inceptioncloud.dragonfly.ui.taskbar.Taskbar
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.*
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.realms.RealmsBridge
import net.minecraft.util.ResourceLocation
import java.net.URL
import javax.imageio.ImageIO

class IngameMenuUI : GuiScreen() {

    override var isNativeResolution: Boolean = true

    override fun initGui() {
        val playerSkullTexture = runBlocking {
            AccountManagerApp.selectedAccount?.retrieveSkull()?.let {
                DynamicTexture(it)
            } ?: kotlin.runCatching {
                DynamicTexture(ImageIO.read(URL(
                    "https://crafatar.com/avatars/${Minecraft.getMinecraft().session.playerID}?size=200&default=MHF_Steve"
                )))
            }.getOrNull()
        }

        +Rectangle {
            x = 0.0
            y = 0.0
            width = this@IngameMenuUI.width.toDouble()
            height = this@IngameMenuUI.height.toDouble()
            color = WidgetColor(0.0, 0.0, 0.0, 0.0)
            morph(30, EaseQuad.IN_OUT, ::color to WidgetColor(0.0, 0.0, 0.0, 0.8))?.start()
        } id "background-fill"

        +Image {
            x = 10.0
            y = 10.0
            width = 50.0
            height = 50.0
            dynamicTexture = playerSkullTexture
            resourceLocation = ResourceLocation("dragonflyres/icons/mainmenu/steve-skull.png")
        } id "player-skull"

        +TextField {
            x = 75.0
            y = 10.0
            width = 300.0
            height = 50.0
            staticText = mc.session.username
            Dragonfly.fontManager.defaultFont.bindFontRenderer(size = 50, useScale = false)
            textAlignVertical = Alignment.CENTER
        } id "player-name"

        +Image {
            val aspectRatio = 617.0 / 96.0

            resourceLocation = ResourceLocation("dragonflyres/logos/ingame-menu.png")
            height = 96.0
            width = height * aspectRatio
            x = this@IngameMenuUI.width / 2.0 - width / 2.0
            y = 70.0
        } id "brand-icon"

        +LoginStatusWidget {
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 50, useScale = false)
            width = fontRenderer!!.getStringWidth(Dragonfly.account?.username ?: "Login") + 50.0
            x = this@IngameMenuUI.width - width - 10.0
            y = 10.0
        } id "login-status"

        +DragonflyButton {
            width = 620.0
            height = 60.0
            x = this@IngameMenuUI.width / 2.0 - width / 2.0
            y = 500.0
            text = "Back to game"
            icon = ImageResource(ResourceLocation("dragonflyres/icons/ingamemenu/resume.png"))
            useScale = false

            onClick {
                mc.displayGuiScreen(null)
            }
        } id "resume-button"

        +DragonflyButton {
            positionBelow("resume-button", 10.0)

            text = "Options"
            icon = ImageResource(ResourceLocation("dragonflyres/icons/mainmenu/options.png"))
            useScale = false

            onClick {
                mc.displayGuiScreen(GuiOptions(this@IngameMenuUI, mc.gameSettings))
            }
        } id "options-button"

        +DragonflyButton {
            positionBelow("options-button", 10.0)

            text = "Quit game"
            icon = ImageResource(ResourceLocation("dragonflyres/icons/ingamemenu/disconnect.png"))
            useScale = false

            onClick {
                if (mc.isIntegratedServerRunning) {
                    disconnect()
                } else {
                    val modal = ConfirmModal(
                        title = "Confirm disconnect",
                        description = "Are you sure that you want to disconnect from this server? Depending on the server's rules, you may " +
                                "lose your progress or get punished for leaving the game. \n\n§7You can disable this warning in the Dragonfly settings.",
                        yesText = "Disconnect",
                        noText = "Keep me playing"
                    ) { if (it) disconnect() }
                    Modal.showModal(modal)
                }
            }
        } id "quit-button"

        Taskbar.initializeTaskbar(this)
    }

    /**
     * Performs the disconnection from the server or stops the integrated server.
     */
    private fun disconnect(): ListenableFuture<Any> = mc.addScheduledTask {
        val isIntegratedServerRunning = mc.isIntegratedServerRunning
        val isConnectedToRealms = mc.isConnectedToRealms

        mc.theWorld.sendQuittingDisconnectingPacket()
        mc.loadWorld(null)

        when {
            isIntegratedServerRunning -> mc.displayGuiScreen(MainMenuUI())
            isConnectedToRealms -> {
                val realmsBridge = RealmsBridge()
                realmsBridge.switchToRealms(MainMenuUI())
            }
            else -> mc.displayGuiScreen(GuiMultiplayer(MainMenuUI()))
        }
    }
}

