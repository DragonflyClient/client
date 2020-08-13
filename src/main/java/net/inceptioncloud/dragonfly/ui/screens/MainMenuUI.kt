package net.inceptioncloud.dragonfly.ui.screens

import com.google.gson.JsonParser
import net.inceptioncloud.dragonfly.engine.internal.ImageResource
import net.inceptioncloud.dragonfly.engine.internal.SizedImage
import net.inceptioncloud.dragonfly.engine.widgets.assembled.ResponsiveImage
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.util.ResourceLocation
import java.net.URL
import javax.imageio.ImageIO

class MainMenuUI : GuiScreen() {

    override var backgroundImage: SizedImage? = loadSplashImage()

    override fun initGui() {
        +ResponsiveImage {
            resourceLocation = ResourceLocation("dragonflyres/logos/branded-name.png")
            originalWidth = 563.0
            originalHeight = 211.0
            width = 140.0
            height = 60.0
            x = width / 2.0 - 70.0
            y = 10.0
        } id "brand-icon"
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

