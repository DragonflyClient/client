package net.inceptioncloud.dragonfly.ui.screens

import com.google.common.util.concurrent.ListenableFuture
import javafx.scene.text.TextAlignment
import kotlinx.coroutines.runBlocking
import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.account.LoginStatusWidget
import net.inceptioncloud.dragonfly.apps.accountmanager.AccountManagerApp
import net.inceptioncloud.dragonfly.apps.spotifyintergration.backend.SpotifyDoAction
import net.inceptioncloud.dragonfly.apps.spotifyintergration.frontend.SpotifyOverlay
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.font.FontWeight
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseQuad
import net.inceptioncloud.dragonfly.engine.tooltip.Tooltip
import net.inceptioncloud.dragonfly.engine.tooltip.TooltipAlignment
import net.inceptioncloud.dragonfly.engine.widgets.assembled.*
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle
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
import java.text.SimpleDateFormat
import java.util.*
import javax.imageio.ImageIO

class IngameMenuUI : GuiScreen() {

    override var isNativeResolution: Boolean = true

    private var adding = true // Whether it is the first reload of the Spotify Overlay or not

    var initialized = false // Whether this is gui is initialized or not

    fun reloadSpotifyOverlay() {

        this.stage.add(Pair("spotify-background", Rectangle().apply {
            width = if(!adding) {
                600.0
            }else {
                160.0
            }
            height = 160.0
            x = if(!adding) {
                this@IngameMenuUI.width - 630.0
            }else {
                this@IngameMenuUI.width - 30.0 - 160.0
            }
            y = 30.0
            color = DragonflyPalette.background
        }))
        this.stage.add(Pair("spotify-image", Image().apply {
            width = 160.0
            height = 160.0
            x = this@IngameMenuUI.width - 30.0 - 160.0
            y = 30.0
            resourceLocation = SpotifyOverlay.coverLocation
        }))
        this.stage.add(Pair("spotify-imageOverlay", Rectangle().apply {
            width = 160.0
            height = 160.0
            x = this@IngameMenuUI.width - 30.0 - 160.0
            y = 30.0
            color = WidgetColor(0.0, 0.0, 0.0, 0.6)
        }))
        this.stage.add(Pair("spotify-title", TextField().apply {
            staticText = Dragonfly.spotifyManager.filterTrackName(Dragonfly.spotifyManager.title)
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(
                fontWeight = FontWeight.MEDIUM, size = if (Dragonfly.fontManager.defaultFont.fontRenderer(
                        fontWeight = FontWeight.MEDIUM, size = 80
                    ).getStringWidth(staticText) > 500.0
                ) {
                    60
                } else {
                    80
                }
            )
            width = if(!adding) {
                440.0
            }else {
                500.0
            }
            height = if(!adding) {
                75.0
            }else {
                100.0
            }
            x = if(!adding) {
                this@IngameMenuUI.width - 630.0
            }else {
                this@IngameMenuUI.width - 7.5 - 175.0
            }
            y = if(!adding) {
                35.0
            }else {
                87.0
            }
            textAlignHorizontal = Alignment.CENTER
        }))
        this.stage.add(Pair("spotify-artist", TextField().apply {
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(
                fontWeight = FontWeight.REGULAR, size = if (staticText.length > 20) {
                    25
                } else {
                    35
                }
            )
            width = if(!adding) {
                440.0
            }else {
                250.0
            }
            height = 75.0
            x = if(!adding) {
                this@IngameMenuUI.width - 630.0
            }else {
                this@IngameMenuUI.width - 162.0
            }
            y = if(!adding) {
                75.0
            }else {
                118.0
            }
            staticText = Dragonfly.spotifyManager.filterArtistName(Dragonfly.spotifyManager.artist)
            color = DragonflyPalette.accentNormal
            textAlignHorizontal = Alignment.CENTER
        }))
        this.stage.add(Pair("spotify-pause", Image().apply {
            x = this@IngameMenuUI.width - 425.0
            y = 102.5
            width = 35.0
            height = 35.0
            resourceLocation = if (Dragonfly.spotifyManager.isPlaying) {
                ResourceLocation("dragonflyres/icons/spotifyintergration/pause.png")
            } else {
                ResourceLocation("dragonflyres/icons/spotifyintergration/play.png")
            }
            this.color = if(!adding) {
                WidgetColor(1.0, 1.0, 1.0, 1.0)
            }else {
                WidgetColor(1.0, 1.0, 1.0, 0.0)
            }
            clickAction = {
                if (Dragonfly.spotifyManager.isPlaying) {
                    Dragonfly.spotifyManager.performDoAction(SpotifyDoAction.PAUSE, null)
                    Dragonfly.spotifyManager.isPlaying = false
                } else {
                    Dragonfly.spotifyManager.performDoAction(SpotifyDoAction.PLAY, null)
                    Dragonfly.spotifyManager.isPlaying = true
                }
                reloadSpotifyOverlay()
            }
        }))
        this.stage.add(Pair("spotify-shuffle", Image().apply {
            x = this@IngameMenuUI.width - 415.0 - 70.0
            y = 110.0
            width = 25.0
            height = 25.0
            resourceLocation = ResourceLocation("dragonflyres/icons/spotifyintergration/shuffle.png")
            this.color = if(!adding) {
                if (Dragonfly.spotifyManager.shuffle) {
                    DragonflyPalette.accentNormal
                } else {
                    WidgetColor(1.0, 1.0, 1.0, 1.0)
                }
            }else {
                WidgetColor(1.0, 1.0, 1.0, 0.0)
            }
            clickAction = {
                val value = !Dragonfly.spotifyManager.shuffle
                Dragonfly.spotifyManager.performDoAction(SpotifyDoAction.SHUFFLE, value.toString())
                Dragonfly.spotifyManager.shuffle = value
                morphSpotifyOverlay()
            }
        }))
        this.stage.add(Pair("spotify-loop", Image().apply {
            x = this@IngameMenuUI.width - 415.0 + 50.0
            y = 110.0
            width = 25.0
            height = 25.0
            resourceLocation = if (Dragonfly.spotifyManager.loop == "TRACK") {
                ResourceLocation("dragonflyres/icons/spotifyintergration/loop_one.png")
            } else {
                ResourceLocation("dragonflyres/icons/spotifyintergration/loop.png")
            }
            this.color = if(!adding) {
                if (Dragonfly.spotifyManager.loop == "OFF" || Dragonfly.spotifyManager.loop == "TRACK") {
                    WidgetColor(1.0, 1.0, 1.0, 1.0)
                } else {
                    DragonflyPalette.accentNormal
                }
            }else {
                WidgetColor(1.0, 1.0, 1.0, 0.0)
            }
            clickAction = {
                when (Dragonfly.spotifyManager.loop) {
                    "OFF" -> {
                        Dragonfly.spotifyManager.performDoAction(SpotifyDoAction.LOOP, "CONTEXT")
                        Dragonfly.spotifyManager.loop = "CONTEXT"
                    }
                    "CONTEXT" -> {
                        Dragonfly.spotifyManager.performDoAction(SpotifyDoAction.LOOP, "TRACK")
                        Dragonfly.spotifyManager.loop = "TRACK"
                    }
                    "TRACK" -> {
                        Dragonfly.spotifyManager.performDoAction(SpotifyDoAction.LOOP, "OFF")
                        Dragonfly.spotifyManager.loop = "OFF"
                    }
                }
                reloadSpotifyOverlay()
            }
        }))
        this.stage.add(Pair("spotify-previous", Image().apply {
            x = this@IngameMenuUI.width - 415.0 - 130.0
            y = 110.0
            width = 22.5
            height = 22.5
            resourceLocation = ResourceLocation("dragonflyres/icons/spotifyintergration/previous.png")
            this.color = if(!adding) {
                WidgetColor(1.0, 1.0, 1.0, 1.0)
            }else {
                WidgetColor(1.0, 1.0, 1.0, 0.0)
            }
            clickAction = {
                Dragonfly.spotifyManager.performDoAction(SpotifyDoAction.PREVIOUS, null)
                Dragonfly.spotifyManager.manualUpdate()
            }
        }))
        this.stage.add(Pair("spotify-skip", Image().apply {
            x = this@IngameMenuUI.width - 415.0 + 110.0
            y = 110.0
            width = 22.5
            height = 22.5
            resourceLocation = ResourceLocation("dragonflyres/icons/spotifyintergration/skip.png")
            this.color = if(!adding) {
                WidgetColor(1.0, 1.0, 1.0, 1.0)
            }else {
                WidgetColor(1.0, 1.0, 1.0, 0.0)
            }
            clickAction = {
                Dragonfly.spotifyManager.performDoAction(SpotifyDoAction.NEXT, null)
                Dragonfly.spotifyManager.manualUpdate()
            }
        }))
        this.stage.add(Pair("spotify-slider", NumberSlider().apply {
            currentValue = Dragonfly.spotifyManager.songCur.toDouble()
            x = this@IngameMenuUI.width - 415.0 - 125.0
            y = 155.0
            width = 270.0
            height = 6.0
            min = 0.0
            max = Dragonfly.spotifyManager.songMax.toDouble()
            liveUpdate = true
            onChange = {
                Dragonfly.spotifyManager.songCur = it.toLong()
                Dragonfly.spotifyManager.performDoAction(SpotifyDoAction.SEEK, it.toString())
                reloadSpotifyOverlay()
            }
            color = if (adding) {
                WidgetColor(1.0, 1.0, 1.0, 0.0)
            } else {
                DragonflyPalette.foreground
            }
            lineColor = if (adding) {
                WidgetColor(1.0, 1.0, 1.0, 0.0)
            } else {
                DragonflyPalette.foreground
            }
            sliderInnerColor = if (adding) {
                WidgetColor(1.0, 1.0, 1.0, 0.0)
            } else {
                DragonflyPalette.accentNormal
            }
            sliderOuterColor = if (adding) {
                WidgetColor(1.0, 1.0, 1.0, 0.0)
            } else {
                DragonflyPalette.background
            }
            textColor = WidgetColor(1.0, 1.0, 1.0, 0.0)
        }))
        this.stage.add(Pair("spotify-songCur", TextField().apply {
            x = this@IngameMenuUI.width - 415.0 - 195.0
            y = 142.0
            width = 75.0
            staticText = SimpleDateFormat("mm:ss").format(Dragonfly.spotifyManager.songCur)
            color = if(!adding) {
                WidgetColor(1.0, 1.0, 1.0, 1.0)
            }else {
                WidgetColor(1.0, 1.0, 1.0, 0.0)
            }
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(fontWeight = FontWeight.LIGHT, size = 50)
        }))
        this.stage.add(Pair("spotify-songMax", TextField().apply {
            x = this@IngameMenuUI.width - 415.0 - 75.0 + 233.0
            y = 142.0
            width = 75.0
            staticText = SimpleDateFormat("mm:ss").format(Dragonfly.spotifyManager.songMax)
            color = if(!adding) {
                WidgetColor(1.0, 1.0, 1.0, 1.0)
            }else {
                WidgetColor(1.0, 1.0, 1.0, 0.0)
            }
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(fontWeight = FontWeight.LIGHT, size = 50)
        }))

        if(adding) {
            morphSpotifyOverlay()
        }
    }

    private fun morphSpotifyOverlay() {
        val duration = 100

        this.stage["spotify-background"]?.morph(
            duration,
            EaseQuad.OUT,
            Rectangle::width to 600.0,
            Rectangle::x to this@IngameMenuUI.width - 630.0
        )?.start()
        this.stage["spotify-title"]?.morph(
            duration,
            EaseQuad.OUT,
            TextField::x to this@IngameMenuUI.width - 630.0,
            TextField::y to 35.0,
            TextField::width to 440.0,
            TextField::height to 100.0
        )?.start()
        this.stage["spotify-artist"]?.morph(
            duration,
            EaseQuad.OUT,
            TextField::x to this@IngameMenuUI.width - 630.0,
            TextField::y to 75.0,
            TextField::width to 440.0
        )?.start()

        Thread {
            val secondDuration = duration * 3

            Thread.sleep(secondDuration.toLong())
            this.stage["spotify-pause"]?.morph(
                duration,
                EaseQuad.IN,
                Image::color to WidgetColor(1.0, 1.0, 1.0, 1.0)
            )?.start()
            this.stage["spotify-shuffle"]?.morph(
                duration,
                EaseQuad.IN,
                Image::color to if (Dragonfly.spotifyManager.shuffle) {
                    DragonflyPalette.accentNormal
                } else {
                    WidgetColor(1.0, 1.0, 1.0, 1.0)
                }
            )?.start()
            this.stage["spotify-loop"]?.morph(
                duration,
                EaseQuad.IN,
                Image::color to if (Dragonfly.spotifyManager.loop == "OFF" || Dragonfly.spotifyManager.loop == "TRACK") {
                    WidgetColor(1.0, 1.0, 1.0, 1.0)
                } else {
                    DragonflyPalette.accentNormal
                }
            )?.start()
            this.stage["spotify-previous"]?.morph(
                duration,
                EaseQuad.IN,
                Image::color to WidgetColor(1.0, 1.0, 1.0, 1.0)
            )?.start()
            this.stage["spotify-skip"]?.morph(
                duration,
                EaseQuad.IN,
                Image::color to WidgetColor(1.0, 1.0, 1.0, 1.0)
            )?.start()
            this.stage["spotify-slider"]?.morph(
                duration,
                EaseQuad.IN,
                NumberSlider::color to DragonflyPalette.foreground,
                NumberSlider::lineColor to DragonflyPalette.foreground,
                NumberSlider::sliderInnerColor to DragonflyPalette.accentNormal,
                NumberSlider::sliderOuterColor to DragonflyPalette.background
            )?.start()
            this.stage["spotify-songCur"]?.morph(
                duration,
                EaseQuad.IN,
                TextField::color to WidgetColor(1.0, 1.0, 1.0, 1.0)
            )?.start()
            this.stage["spotify-songMax"]?.morph(
                duration,
                EaseQuad.IN,
                TextField::color to WidgetColor(1.0, 1.0, 1.0, 1.0)
            )?.start()
        }.start()

        adding = false
    }

    override fun initGui() {
        val playerSkullTexture = runBlocking {
            AccountManagerApp.selectedAccount?.retrieveSkull()?.let {
                DynamicTexture(it)
            } ?: kotlin.runCatching {
                DynamicTexture(
                    ImageIO.read(
                        URL(
                            "https://crafatar.com/avatars/${Minecraft.getMinecraft().session.playerID}?size=200&default=MHF_Steve"
                        )
                    )
                )
            }.getOrNull()
        }

        +Rectangle {
            val isOpenedFromIngame = mc.previousScreen == null

            x = 0.0
            y = 0.0
            width = this@IngameMenuUI.width.toDouble()
            height = this@IngameMenuUI.height.toDouble()
            color = WidgetColor(0.0, 0.0, 0.0, if (isOpenedFromIngame) 0.0 else 0.8)

            if (isOpenedFromIngame) {
                morph(30, EaseQuad.IN_OUT, ::color to WidgetColor(0.0, 0.0, 0.0, 0.8))?.start()
            }
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
            y = 110.0
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
            y = 430.0
            text = "Back to game"
            icon = ImageResource(ResourceLocation("dragonflyres/icons/ingamemenu/resume.png"))
            iconSize = height - 15.0
            useScale = false

            onClick {
                mc.displayGuiScreen(null)
            }
        } id "resume-button"

        +DragonflyButton {
            positionBelow("resume-button", 10.0)

            text = "Options"
            icon = ImageResource(ResourceLocation("dragonflyres/icons/mainmenu/options.png"))
            iconSize = height - 15.0
            useScale = false

            onClick {
                mc.displayGuiScreen(GuiOptions(this@IngameMenuUI, mc.gameSettings))
            }
        } id "options-button"

        +DragonflyButton {
            positionBelow("options-button", 10.0)

            text = "Quit game"
            icon = ImageResource(ResourceLocation("dragonflyres/icons/ingamemenu/disconnect.png"))
            iconSize = height - 20.0
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

        val buttonSize = 60.0
        val buttonGap = 35.0

        val minorActions = getMinorActions()
        val actionsWidth = minorActions.size * buttonSize + (minorActions.size - 1) * buttonGap
        var xPosition = width / 2.0 - actionsWidth / 2.0

        for (widget in minorActions) {
            +widget.apply {
                positionBelow("quit-button", 60.0)
                x = xPosition
                width = buttonSize
                height = buttonSize
            } id widget.tooltip!!.text.toLowerCase().replace(" ", "-")
            xPosition += buttonSize + buttonGap
        }

        Taskbar.initializeTaskbar(this)
        reloadSpotifyOverlay()

        adding = true
        initialized = true
        SpotifyOverlay.hide = true
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

    override fun onGuiClosed() {
        super.onGuiClosed()

        SpotifyOverlay.hide = false
    }

}

