package net.inceptioncloud.dragonfly.ui.screens

import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.*
import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.account.LoginStatusWidget
import net.inceptioncloud.dragonfly.apps.accountmanager.AccountManagerApp
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseQuad
import net.inceptioncloud.dragonfly.engine.tooltip.Tooltip
import net.inceptioncloud.dragonfly.engine.tooltip.TooltipAlignment
import net.inceptioncloud.dragonfly.engine.widgets.assembled.*
import net.inceptioncloud.dragonfly.engine.font.Typography
import net.inceptioncloud.dragonfly.engine.font.font
import net.inceptioncloud.dragonfly.engine.widgets.primitive.*
import net.inceptioncloud.dragonfly.options.sections.OptionsSectionClient
import net.inceptioncloud.dragonfly.overlay.modal.Modal
import net.inceptioncloud.dragonfly.ui.modal.ConfirmModal
import net.inceptioncloud.dragonfly.ui.taskbar.Taskbar
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.*
import net.minecraft.client.gui.achievement.GuiAchievements
import net.minecraft.client.gui.achievement.GuiStats
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.realms.RealmsBridge
import net.minecraft.util.ResourceLocation
import java.net.URL
import javax.imageio.ImageIO

class IngameMenuUI : GuiScreen() {

    override var isNativeResolution: Boolean = true

    override fun initGui() {
        +Rectangle {
            val isOpenedFromIngame = mc.previousScreen == null

            x = 0.0f
            y = 0.0f
            width = this@IngameMenuUI.width.toFloat()
            height = this@IngameMenuUI.height.toFloat()
            color = WidgetColor(0.0, 0.0, 0.0, if (isOpenedFromIngame) 0.0 else 0.8)

            if (isOpenedFromIngame) {
                morph(30, EaseQuad.IN_OUT, ::color to WidgetColor(0.0, 0.0, 0.0, 0.8))?.start()
            }
        } id "background-fill"

        +Image {
            x = 10.0f
            y = 10.0f
            width = 50.0f
            height = 50.0f
            resourceLocation = ResourceLocation("dragonflyres/icons/mainmenu/steve-skull.png")
            bindLazyTexture {
                AccountManagerApp.selectedAccount?.retrieveSkull()?.let { DynamicTexture(it) }
            }
        } id "player-skull"

        +TextField {
            x = 75.0f
            y = 10.0f
            width = 300.0f
            height = 50.0f
            staticText = mc.session.username
            Dragonfly.fontManager.defaultFont.bindFontRenderer(size = 50)
            textAlignVertical = Alignment.CENTER
        } id "player-name"

        +Image {
            val aspectRatio = 617 / 96.0f

            resourceLocation = ResourceLocation("dragonflyres/logos/ingame-menu.png")
            height = 96.0f
            width = height * aspectRatio
            x = this@IngameMenuUI.width / 2 - width / 2
            y = 110.0f
        } id "brand-icon"

        +LoginStatusWidget {
            fontRenderer = font(Typography.BASE)
            width = fontRenderer!!.getStringWidth(Dragonfly.account?.username ?: "Login") + 50.0f
            x = this@IngameMenuUI.width - width - 10.0f
            y = 10.0f
        } id "login-status"

        +DragonflyButton {
            width = 620.0f
            height = 60.0f
            x = this@IngameMenuUI.width / 2 - width / 2
            y = 430.0f
            text = "Back to game"
            icon = ImageResource(ResourceLocation("dragonflyres/icons/ingamemenu/resume.png"))
            iconSize = height - 15.0f
            useScale = false

            onClick {
                mc.displayGuiScreen(null)
            }
        } id "resume-button"

        +DragonflyButton {
            positionBelow("resume-button", 10.0f)

            text = "Options"
            icon = ImageResource(ResourceLocation("dragonflyres/icons/mainmenu/options.png"))
            iconSize = height - 15.0f
            useScale = false

            onClick {
                mc.displayGuiScreen(GuiOptions(this@IngameMenuUI, mc.gameSettings))
            }
        } id "options-button"

        +DragonflyButton {
            positionBelow("options-button", 10.0f)

            text = "Quit game"
            icon = ImageResource(ResourceLocation("dragonflyres/icons/ingamemenu/disconnect.png"))
            iconSize = height - 20.0f
            useScale = false

            onClick {
                if (mc.isIntegratedServerRunning || OptionsSectionClient.confirmDisconnect() != true) {
                    disconnect()
                } else {
                    val modal = ConfirmModal(
                        title = "Confirm disconnect",
                        description = "Are you sure that you want to disconnect from this server? Depending on the server's rules, you may " +
                                "lose your progress or get punished for leaving the game. \n\n§7You can disable this warning in the Dragonfly settings.",
                        yesText = "Disconnect",
                        noText = "Never mind"
                    ) { if (it) disconnect() }
                    Modal.showModal(modal)
                }
            }
        } id "quit-button"

        val buttonSize = 60.0f
        val buttonGap = 35.0f

        val minorActions = getMinorActions()
        val actionsWidth = minorActions.size * buttonSize + (minorActions.size - 1) * buttonGap
        var xPosition = width / 2 - actionsWidth / 2

        for (widget in minorActions) {
            +widget.apply {
                positionBelow("quit-button", 60.0f)
                x = xPosition
                width = buttonSize
                height = buttonSize
            } id widget.tooltip!!.text.toLowerCase().replace(" ", "-")
            xPosition += buttonSize + buttonGap
        }

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
            isIntegratedServerRunning -> mc.displayGuiScreen(GuiSelectWorld(MainMenuUI()))
            isConnectedToRealms -> {
                val realmsBridge = RealmsBridge()
                realmsBridge.switchToRealms(MainMenuUI())
            }
            else -> mc.displayGuiScreen(GuiMultiplayer(MainMenuUI()))
        }
    }

    /**
     * Returns a list of minor actions that are available as [RoundIconButton]s below
     * the major actions. Depending on whether the game is currently in a singleplayer
     * world, the "Share to LAN" button is visible.
     */
    private fun getMinorActions(): List<RoundIconButton> {
        val list = mutableListOf<RoundIconButton>()
        if (mc.isIntegratedServerRunning) {
            list.add(RoundIconButton().apply {
                icon = ImageResource("dragonflyres/icons/ingamemenu/lan-share.png")
                tooltip = Tooltip("Share to LAN", TooltipAlignment.BELOW)
                clickAction = {
                    mc.displayGuiScreen(GuiShareToLan(this@IngameMenuUI))
                }
            })
        }
        list.add(RoundIconButton().apply {
            icon = ImageResource("dragonflyres/icons/ingamemenu/achievements.png")
            tooltip = Tooltip("Achievements", TooltipAlignment.BELOW)
            clickAction = {
                mc.displayGuiScreen(GuiAchievements(this@IngameMenuUI, mc.thePlayer.statFileWriter))
            }
        })
        list.add(RoundIconButton().apply {
            icon = ImageResource("dragonflyres/icons/ingamemenu/statistics.png")
            tooltip = Tooltip("Statistics", TooltipAlignment.BELOW)
            clickAction = {
                mc.displayGuiScreen(GuiStats(this@IngameMenuUI, mc.thePlayer.statFileWriter))
            }
        })
        return list
    }
}

