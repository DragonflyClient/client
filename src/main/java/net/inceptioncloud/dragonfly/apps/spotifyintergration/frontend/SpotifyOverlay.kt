package net.inceptioncloud.dragonfly.apps.spotifyintergration.frontend

import com.google.common.hash.Hashing
import com.google.gson.JsonParser
import com.sun.org.apache.bcel.internal.util.SecuritySupport
import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.cosmetics.types.capes.CapeManager
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.font.FontWeight
import net.inceptioncloud.dragonfly.engine.internal.Alignment
import net.inceptioncloud.dragonfly.engine.internal.WidgetColor
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.IImageBuffer
import net.minecraft.client.renderer.ThreadDownloadImageData
import net.minecraft.util.ResourceLocation
import org.apache.logging.log4j.LogManager
import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
import java.io.FileOutputStream

import java.io.InputStream
import java.io.OutputStream
import java.lang.IllegalArgumentException
import java.nio.charset.Charset

object SpotifyOverlay {

    lateinit var image: Image
    lateinit var imageOverlay: Rectangle
    lateinit var title: TextField
    lateinit var artist: TextField
    lateinit var timeLine: Rectangle
    lateinit var timeCur: Rectangle

    var hide = false
    var adding = true

    var coverLocation = ResourceLocation("dragonflyres/icons/spotifyintergration/no-track-found.png")

    private fun updateCoverTexture() {
        if(Dragonfly.spotifyManager.imageUrl != "") {
            downloadSongCoverResource(Dragonfly.spotifyManager.imageUrl)
        }else {
            coverLocation = ResourceLocation("dragonflyres/icons/spotifyintergration/no-track-found.png")
        }
    }

    fun update() {

        if (Dragonfly.spotifyManager.updateImage || adding || Dragonfly.spotifyManager.imageUrl == "") {
            updateCoverTexture()
            Dragonfly.spotifyManager.updateImage = false
            adding = false
        }

        hide = Dragonfly.spotifyManager.title == "Nothing playing"

        image = Image().apply {
            width = 50.0
            height = 50.0
            x = ScaledResolution(Minecraft.getMinecraft()).scaledWidth - 10.0 - width
            y = 10.0
            resourceLocation = coverLocation
        }

        imageOverlay = Rectangle().apply {
            width = image.width
            height = image.height
            x = image.x
            y = image.y
            color = WidgetColor(0.0, 0.0, 0.0, 0.6)
        }

        title = TextField().apply {
            staticText = Dragonfly.spotifyManager.filterTrackName(Dragonfly.spotifyManager.title)
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(
                fontWeight = FontWeight.MEDIUM, size = if (Dragonfly.fontManager.defaultFont.fontRenderer(
                        fontWeight = FontWeight.MEDIUM, size = 12
                    ).getStringWidth(staticText) > 50.0
                ) {
                    8
                } else if (Dragonfly.fontManager.defaultFont.fontRenderer(
                        fontWeight = FontWeight.MEDIUM, size = 20
                    ).getStringWidth(staticText) > 50.0
                ) {
                    12
                } else {
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

    private fun downloadSongCoverResource(imageUrl: String) {
        @Suppress("UnstableApiUsage")
        val urlHash = Hashing.goodFastHash(3).hashString(imageUrl, Charset.defaultCharset()).toString()
        val resourceLocation = ResourceLocation("spotify/cover/$urlHash")
        val textureManager = Minecraft.getMinecraft().textureManager
        val texture = textureManager.getTexture(resourceLocation)
        val titleToDownload = Dragonfly.spotifyManager.title

        coverLocation = ResourceLocation("dragonflyres/icons/spotifyintergration/no-track-found.png")

        if (texture is ThreadDownloadImageData) {
            if (texture.imageFound != null) {
                if (texture.imageFound && Dragonfly.spotifyManager.title == titleToDownload) {
                    coverLocation = resourceLocation
                }
                return
            }
        }

        val imageBuffer: IImageBuffer = object : IImageBuffer {
            override fun parseUserSkin(image: BufferedImage): BufferedImage {
                return image
            }

            override fun skinAvailable() {
                if (Dragonfly.spotifyManager.title == titleToDownload) {
                    LogManager.getLogger().info("Downloaded song cover of '${Dragonfly.spotifyManager.filterTrackName(Dragonfly.spotifyManager.title)}'")
                    coverLocation = resourceLocation
                }
            }
        }

        val download = ThreadDownloadImageData(null, imageUrl, null, imageBuffer)
        download.pipeline = true
        textureManager.loadTexture(resourceLocation, download)
    }

}