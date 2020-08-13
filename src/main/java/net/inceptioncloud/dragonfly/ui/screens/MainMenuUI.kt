package net.inceptioncloud.dragonfly.ui.screens

import com.google.gson.JsonParser
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.widgets.assembled.DragonflyButton
import net.inceptioncloud.dragonfly.engine.widgets.assembled.ResponsiveImage
import net.inceptioncloud.dragonfly.ui.taskbar.Taskbar
import net.minecraft.client.gui.*
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.util.ResourceLocation
import java.net.URL
import javax.imageio.ImageIO

class MainMenuUI : GuiScreen() {

    override var backgroundImage: SizedImage? = loadSplashImage()

    override fun initGui() {
        +ResponsiveImage {
            resourceLocation = ResourceLocation("dragonflyres/logos/branded-name.png")
            originalWidth = 1118.0
            originalHeight = 406.0
            width = 140.0
            height = 60.0
            x = this@MainMenuUI.width / 2.0 - 70.0
            y = 10.0
        } id "brand-icon"

        val buttonsY = (height / 2.0 - 40.0).coerceAtLeast(100.0)

        +DragonflyButton {
            x = this@MainMenuUI.width / 2.0 - 100.0
            y = buttonsY
            text = "Singleplayer"
            icon = ImageResource(ResourceLocation("dragonflyres/icons/mainmenu/singleplayer.png"))

            onClick {
                mc.displayGuiScreen(GuiSelectWorld(this@MainMenuUI))
            }
        } id "singleplayer-button"

        +DragonflyButton {
            positionBelow("singleplayer-button", 5.0)

            text = "Multiplayer"
            icon = ImageResource(ResourceLocation("dragonflyres/icons/mainmenu/multiplayer.png"))

            onClick {
                mc.displayGuiScreen(GuiMultiplayer(this@MainMenuUI))
            }
        } id "multiplayer-button"

        +DragonflyButton {
            positionBelow("multiplayer-button", 5.0)

            text = "Options"
            icon = ImageResource(ResourceLocation("dragonflyres/icons/mainmenu/options.png"))

            onClick {
                mc.displayGuiScreen(GuiOptions(this@MainMenuUI, mc.gameSettings))
            }
        } id "options-button"

        Taskbar.initializeTaskbar(this)
    }

    /**
     * Load the splash image and its properties from the Dragonfly webserver and creates a [SizedImage]
     * based on them.
     */
    private fun loadSplashImage(): SizedImage {
        val image = ImageIO.read(URL("https://cdn.icnet.dev/dragonfly/splash/image.png"))
        val properties = JsonParser().parse(
            URL("https://cdn.icnet.dev/dragonfly/splash/properties.json").readText()
        ).asJsonObject

        val width = properties.get("width").asInt.toDouble()
        val height = properties.get("height").asInt.toDouble()

        return SizedImage(ImageResource(DynamicTexture(image)), width, height)
    }
}

