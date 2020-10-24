package net.inceptioncloud.dragonfly.ui.screens

import com.google.gson.JsonParser
import kotlinx.coroutines.runBlocking
import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.account.LoginStatusWidget
import net.inceptioncloud.dragonfly.apps.accountmanager.AccountManagerApp
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.widgets.assembled.DragonflyButton
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.inceptioncloud.dragonfly.engine.font.Typography
import net.inceptioncloud.dragonfly.engine.font.font
import net.inceptioncloud.dragonfly.ui.taskbar.Taskbar
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.*
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.util.ResourceLocation
import org.apache.logging.log4j.LogManager
import java.net.URL
import javax.imageio.ImageIO

class MainMenuUI : GuiScreen() {

    override var backgroundImage: SizedImage? = splashImage

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
            x = 10.0f
            y = 10.0f
            width = 50.0f
            height = 50.0f
            dynamicTexture = playerSkullTexture
            resourceLocation = ResourceLocation("dragonflyres/icons/mainmenu/steve-skull.png")
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
            val aspectRatio = 1118 / 406.0f

            resourceLocation = ResourceLocation("dragonflyres/logos/branded-name.png")
            height = 200.0f
            width = height * aspectRatio
            x = this@MainMenuUI.width / 2 - width / 2
            y = 70.0f
        } id "brand-icon"

        +LoginStatusWidget {
            fontRenderer = font(Typography.BASE)
            width = fontRenderer!!.getStringWidth(Dragonfly.account?.username ?: "Login") + 50.0f
            x = this@MainMenuUI.width - width - 10.0f
            y = 10.0f
        } id "login-status"

        +DragonflyButton {
            width = 620.0f
            height = 60.0f
            x = this@MainMenuUI.width / 2 - width / 2
            y = 500.0f
            text = "Singleplayer"
            icon = ImageResource(ResourceLocation("dragonflyres/icons/mainmenu/singleplayer.png"))
            useScale = false

            onClick {
                mc.displayGuiScreen(GuiSelectWorld(this@MainMenuUI))
            }
        } id "singleplayer-button"

        +DragonflyButton {
            positionBelow("singleplayer-button", 10.0f)

            text = "Multiplayer"
            icon = ImageResource(ResourceLocation("dragonflyres/icons/mainmenu/multiplayer.png"))
            useScale = false

            onClick {
                mc.displayGuiScreen(GuiMultiplayer(this@MainMenuUI))
            }
        } id "multiplayer-button"

        +DragonflyButton {
            positionBelow("multiplayer-button", 10.0f)

            text = "Options"
            icon = ImageResource(ResourceLocation("dragonflyres/icons/mainmenu/options.png"))
            useScale = false

            onClick {
                mc.displayGuiScreen(GuiOptions(this@MainMenuUI, mc.gameSettings))
            }
        } id "options-button"

        Taskbar.initializeTaskbar(this)
    }

    companion object {

        /**
         * Load the splash image and its properties from the Dragonfly webserver and creates a [SizedImage]
         * based on them.
         */
        val splashImage: SizedImage by lazy {
            LogManager.getLogger().info("Downloading splash image...");

            try {
                val image = ImageIO.read(URL("https://cdn.icnet.dev/dragonfly/splash/image.png"))
                val properties = JsonParser().parse(
                    URL("https://cdn.icnet.dev/dragonfly/splash/properties.json").readText()
                ).asJsonObject

                val width = properties.get("width").asInt.toFloat()
                val height = properties.get("height").asInt.toFloat()

                SizedImage(ImageResource(DynamicTexture(image)), width, height)
            } catch (e: Exception) {
                LogManager.getLogger().warn("Could not download splash image! Using offline backup...")
                SizedImage(ImageResource("dragonflyres/splashes/offline-main-menu.png"), 1920.0f, 1080.0f)
            }
        }
    }
}

