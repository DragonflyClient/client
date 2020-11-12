package net.inceptioncloud.dragonfly.apps.spotifyintergration.frontend

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.engine.font.FontWeight
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.util.ResourceLocation

object SpotifyOverlay {

    lateinit var image: Image
    lateinit var title: TextField

    fun update() {

        image = Image().apply {
            width = 30.0
            height = 30.0
            x = ScaledResolution(Minecraft.getMinecraft()).scaledWidth - 10.0 - width
            y = 10.0
            resourceLocation = ResourceLocation("dragonflyres/icons/spotifyintergration/no-track-found.png")
        }

        title = TextField().apply {
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(fontWeight = FontWeight.MEDIUM, size = 10)
            width = 30.0
            x = ScaledResolution(Minecraft.getMinecraft()).scaledWidth - width - 7.0
            y = 17.0
            staticText = "Song Title"
        }

    }

}