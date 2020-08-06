package net.inceptioncloud.dragonfly.ui.screens

import com.google.gson.JsonParser
import net.inceptioncloud.dragonfly.engine.internal.ImageResource
import net.inceptioncloud.dragonfly.engine.internal.SizedImage
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.texture.DynamicTexture
import java.net.URL
import javax.imageio.ImageIO

class MainMenuUI : GuiScreen() {

    override var backgroundImage: SizedImage? = loadSplashImage()

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

