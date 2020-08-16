package net.inceptioncloud.dragonfly.ui.screens

import com.google.gson.JsonParser
import net.inceptioncloud.dragonfly.Dragonfly
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
import java.lang.Double.min
import java.net.URL
import javax.imageio.ImageIO

class MainMenuUI : GuiScreen() {

    /**
     * An image of the player's skull fetched using the crafatar API or null, if an error occurred.
     */
    val playerSkullTexture = try {
        DynamicTexture(ImageIO.read(
            URL("https://crafatar.com/avatars/${Minecraft.getMinecraft().session.playerID}?size=200&default=MHF_Steve")
        ))
    } catch (e: Exception) {
        null
    }

    override var backgroundImage: SizedImage? = splashImage

    override var customScaleFactor: () -> Double? = { min(mc.displayWidth / 1920.0, mc.displayHeight / 1080.0) }

    override fun initGui() {
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
            width = 200.0
            height = 50.0
            staticText = mc.session.username
            font = Dragonfly.fontManager.defaultFont
            fontSize = 50.0
            textAlignVertical = Alignment.CENTER
        } id "player-name"

        +Image {
            val aspectRatio = 1118.0 / 406.0

            resourceLocation = ResourceLocation("dragonflyres/logos/branded-name.png")
            height = 200.0
            width = height * aspectRatio
            x = this@MainMenuUI.width / 2.0 - width / 2.0
            y = 70.0
        } id "brand-icon"

        +TextField {
            staticText = "v1.3.0.0"
            textAlignHorizontal = Alignment.END
            font = Dragonfly.fontManager.defaultFont
            fontSize = 50.0
            color = DragonflyPalette.foreground
            width = 500.0
            padding = 10.0
            x = this@MainMenuUI.width - width
            y = 0.0
            adaptHeight = true
        } id "version"

        +DragonflyButton {
            width = 620.0
            height = 60.0
            x = this@MainMenuUI.width / 2.0 - width / 2.0
            y = 500.0
            text = "Singleplayer"
            icon = ImageResource(ResourceLocation("dragonflyres/icons/mainmenu/singleplayer.png"))

            onClick {
                mc.displayGuiScreen(GuiSelectWorld(this@MainMenuUI))
            }
        } id "singleplayer-button"

        +DragonflyButton {
            positionBelow("singleplayer-button", 10.0)

            text = "Multiplayer"
            icon = ImageResource(ResourceLocation("dragonflyres/icons/mainmenu/multiplayer.png"))

            onClick {
                mc.displayGuiScreen(GuiMultiplayer(this@MainMenuUI))
            }
        } id "multiplayer-button"

        +DragonflyButton {
            positionBelow("multiplayer-button", 10.0)

            text = "Options"
            icon = ImageResource(ResourceLocation("dragonflyres/icons/mainmenu/options.png"))

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
            val image = ImageIO.read(URL("https://cdn.icnet.dev/dragonfly/splash/image.png"))
            val properties = JsonParser().parse(
                URL("https://cdn.icnet.dev/dragonfly/splash/properties.json").readText()
            ).asJsonObject

            val width = properties.get("width").asInt.toDouble()
            val height = properties.get("height").asInt.toDouble()

            SizedImage(ImageResource(DynamicTexture(image)), width, height)
        }
    }
}

