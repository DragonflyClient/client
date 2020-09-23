package net.inceptioncloud.dragonfly.ui.screens

import kotlinx.coroutines.runBlocking
import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.account.LoginStatusWidget
import net.inceptioncloud.dragonfly.apps.accountmanager.AccountManagerApp
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.widgets.assembled.DragonflyButton
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.inceptioncloud.dragonfly.ui.taskbar.Taskbar
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.*
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.util.ResourceLocation
import java.net.URL
import javax.imageio.ImageIO

class IngameMenuUI : GuiScreen() {

    override var backgroundFill: WidgetColor? = DragonflyPalette.background.altered { alphaDouble = 0.5 }

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
                mc.displayGuiScreen(GuiSelectWorld(this@IngameMenuUI))
            }
        } id "resume-button"

        +DragonflyButton {
            positionBelow("resume-button", 10.0)

            text = "Options"
            icon = ImageResource(ResourceLocation("dragonflyres/icons/mainmenu/options.png"))
            useScale = false

            onClick {
                mc.displayGuiScreen(GuiMultiplayer(this@IngameMenuUI))
            }
        } id "options-button"

        +DragonflyButton {
            positionBelow("options-button", 10.0)

            text = "Quit game"
            icon = ImageResource(ResourceLocation("dragonflyres/icons/ingamemenu/disconnect.png"))
            useScale = false

            onClick {
                mc.displayGuiScreen(GuiOptions(this@IngameMenuUI, mc.gameSettings))
            }
        } id "quit-button"

        Taskbar.initializeTaskbar(this)
    }
}

