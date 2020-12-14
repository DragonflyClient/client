package net.inceptioncloud.dragonfly.apps.spotifyintergration.frontend

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.font.FontWeight
import net.inceptioncloud.dragonfly.engine.internal.Alignment
import net.inceptioncloud.dragonfly.engine.internal.WidgetColor
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.util.ResourceLocation

object SpotifyOverlay {

    lateinit var image: Image
    lateinit var imageOverlay: Rectangle
    lateinit var title: TextField
    lateinit var artist: TextField
    lateinit var timeLine: Rectangle
    lateinit var timeCur: Rectangle

    var hide = false

    fun update() {

        image = Image().apply {
            width = 50.0
            height = 50.0
            x = ScaledResolution(Minecraft.getMinecraft()).scaledWidth - 10.0 - width
            y = 10.0
            resourceLocation =
                ResourceLocation("dragonflyres/icons/spotifyintergration/costa_rica.png") // no-track-found
        }

        imageOverlay = Rectangle().apply {
            width = image.width
            height = image.height
            x = image.x
            y = image.y
            color = WidgetColor(0.0, 0.0, 0.0, 0.6)
        }

        var titleSize: Int

        title = TextField().apply {
            staticText = Dragonfly.spotifyManager.filterTrackName(Dragonfly.spotifyManager.title)
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(
                fontWeight = FontWeight.MEDIUM, size = if (Dragonfly.fontManager.defaultFont.fontRenderer(
                        fontWeight = FontWeight.MEDIUM, size = 12
                    ).getStringWidth(staticText) > 50.0
                ) {
                    titleSize = 8
                    8
                } else if (Dragonfly.fontManager.defaultFont.fontRenderer(
                        fontWeight = FontWeight.MEDIUM, size = 20
                    ).getStringWidth(staticText) > 50.0
                ) {
                    titleSize = 12
                    12
                } else {
                    titleSize = 20
                    20
                }
            )
            width = 50.0
            height = 50.0
            x = ScaledResolution(Minecraft.getMinecraft()).scaledWidth - 10.0 - width
            y = 28.0
            textAlignHorizontal = Alignment.CENTER
        }

        artist = TextField().apply {
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(fontWeight = FontWeight.MEDIUM, size = 12)
            width = 50.0
            height = 50.0
            x = ScaledResolution(Minecraft.getMinecraft()).scaledWidth - 10.0 - width
            y = 37.0
            staticText = Dragonfly.spotifyManager.filterArtistName(Dragonfly.spotifyManager.artist)
            color = DragonflyPalette.accentNormal
            textAlignHorizontal = Alignment.CENTER
        }

        timeLine = Rectangle().apply {
            width = image.width
            height = 1.0
            x = image.x
            y = image.y + image.height
            color = DragonflyPalette.background
        }

        timeCur = Rectangle().apply {
            width = (timeLine.width / Dragonfly.spotifyManager.songMax) * Dragonfly.spotifyManager.songCur
            height = timeLine.height
            x = timeLine.x
            y = timeLine.y
            color = DragonflyPalette.accentNormal
        }

    }

}