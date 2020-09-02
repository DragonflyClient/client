package net.inceptioncloud.dragonfly.cosmetics.types.capes

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.cosmetics.logic.CosmeticsManager
import net.inceptioncloud.dragonfly.cosmetics.logic.EnumCosmeticType
import net.inceptioncloud.dragonfly.engine.internal.WidgetColor
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.client.renderer.IImageBuffer
import net.minecraft.client.renderer.ThreadDownloadImageData
import net.minecraft.util.ResourceLocation
import org.apache.logging.log4j.LogManager
import java.awt.Color
import java.awt.image.BufferedImage
import java.net.URL

object CapeManager {

    /**
     * Downloads the cape for the [player] and sets the [AbstractClientPlayer.locationOfCape]
     * after the download has finished. The url is fetched by [getCapeURL] depending on the
     * cosmetic id of the currently active cape.
     */
    @JvmStatic
    fun downloadCape(player: AbstractClientPlayer) {
        val cosmetics = player.cosmetics?.filter { it.enabled }
        val databaseModels = cosmetics?.mapNotNull { CosmeticsManager.getDatabaseModelById(it.cosmeticId) }
        val cape = databaseModels?.firstOrNull { it.get("type").asString == EnumCosmeticType.CAPE.name }
        val cosmeticId = cape?.get("cosmeticId")?.asInt

        if (cape?.has("animated") == true && cape.get("animated").asBoolean) {
            downloadAnimatedCape(player, "https://cdn.icnet.dev/cosmetics/capes/$cosmeticId", cosmeticId!!)
        } else if (cape != null) {
            downloadStaticCape(player, "https://cdn.icnet.dev/cosmetics/capes/$cosmeticId.png", cosmeticId!!)
        }
    }

    private fun downloadStaticCape(player: AbstractClientPlayer, url: String, cosmeticId: Int) {
        val username = player.nameClear
        val capeInfo = JsonParser().parse(URL(url.replace(".png", ".json")).readText()).asJsonObject

        if (username != null && username.isNotEmpty()) {
            LogManager.getLogger().debug("Downloading cape for $username...")

            val resourceLocation = ResourceLocation("capes/static/$cosmeticId")
            val textureManager = Minecraft.getMinecraft().textureManager
            val texture = textureManager.getTexture(resourceLocation)

            if (texture is ThreadDownloadImageData) {
                if (texture.imageFound != null) {
                    if (texture.imageFound) {
                        LogManager.getLogger().info("Cape cached: $resourceLocation")
                        player.locationOfCape = resourceLocation
                    }
                    return
                }
            }

            val imageBuffer: IImageBuffer = object : IImageBuffer {
                override fun parseUserSkin(image: BufferedImage): BufferedImage {
                    return parseCape(applyFillMode(image, capeInfo))
                }

                override fun skinAvailable() {
                    player.locationOfCape = resourceLocation
                }
            }

            val download = ThreadDownloadImageData(null, url, null, imageBuffer)
            download.pipeline = true
            textureManager.loadTexture(resourceLocation, download)
        }
    }

    private fun downloadAnimatedCape(player: AbstractClientPlayer, baseUrl: String, cosmeticId: Int) {
        val username = player.nameClear
        if (username == null || username.isEmpty()) return
        LogManager.getLogger().debug("Downloading animated cape for $username...")

        val capeInfo = JsonParser().parse(URL("$baseUrl/cape_info.json").readText()).asJsonObject
        val frames = capeInfo.get("frames").asInt
        val delay = capeInfo.get("delay").asLong
        val frameUrls = (0 until frames).map { "$baseUrl/$it.png" }
        val animatedCape = AnimatedCape(delay, Array(frames) { null })

        fun yield() {
            if (animatedCape.frames.none { it == null }) {
                LogManager.getLogger().info("All frames collected!");
                player.animatedCape = animatedCape
            }
        }

        for ((frame, url) in frameUrls.withIndex()) {
            val resourceLocation = ResourceLocation("capes/animated/$cosmeticId/$frame")
            val textureManager = Minecraft.getMinecraft().textureManager
            val texture = textureManager.getTexture(resourceLocation)

            if (texture is ThreadDownloadImageData) {
                if (texture.imageFound != null) {
                    if (texture.imageFound) {
                        animatedCape.frames[frame] = resourceLocation
                        yield()
                    }
                    continue
                }
            }

            val imageBuffer: IImageBuffer = object : IImageBuffer {
                override fun parseUserSkin(image: BufferedImage): BufferedImage {
                    return parseCape(applyFillMode(image, capeInfo))
                }

                override fun skinAvailable() {
                    animatedCape.frames[frame] = resourceLocation
                    yield()
                }
            }

            val download = ThreadDownloadImageData(null, url, null, imageBuffer)
            download.pipeline = true
            textureManager.loadTexture(resourceLocation, download)
        }
    }

    private fun parseCape(image: BufferedImage): BufferedImage {
        var i = 64
        var j = 32
        val k = image.width
        val l = image.height
        while (i < k || j < l) {
            i *= 2
            j *= 2
        }
        val cape = BufferedImage(i, j, 2)
        val graphics = cape.graphics
        graphics.drawImage(image, 0, 0, null)
        graphics.dispose()
        return cape
    }

    private fun applyFillMode(src: BufferedImage, capeInfo: JsonObject): BufferedImage {
        val fillMode = if (capeInfo.has("fillMode"))
            Dragonfly.gson.fromJson(capeInfo.get("fillMode"), FillMode::class.java)!!
        else FillMode.NONE

        val fillColor = if (capeInfo.has("fillColor"))
            Dragonfly.gson.fromJson(capeInfo.get("fillColor"), WidgetColor::class.java)!!
        else WidgetColor.DEFAULT

        return when(fillMode) {
            FillMode.WRAP -> wrapContent(src, fillColor)
            FillMode.CROP -> cropContent(src, fillColor)
            FillMode.NONE -> src
        }
    }

    private fun cropContent(src: BufferedImage, fillColor: WidgetColor): BufferedImage {
        val dest = BufferedImage(352, 275, 2)
        val destX = 16
        val destY = 16
        val destWidth = 160
        val destHeight = 259

        val diffWidth = src.width - destWidth
        val diffHeight = src.height - destHeight

        val factor = if (diffWidth < diffHeight) {
            src.width.toDouble() / destWidth
        } else {
            src.height.toDouble() / destHeight
        }

        val scaledWidth = destWidth * factor
        val scaledHeight = destHeight * factor
        val srcX = (src.width - scaledWidth) / 2.0
        val srcY = (src.height - scaledHeight) / 2.0

        val graphics = dest.graphics
        graphics.color = fillColor.base
        graphics.fillRect(0, 0, dest.width, dest.height)
        graphics.color = Color(255, 255, 255, 255)
        graphics.drawImage(
            src, destX, destY, destX + destWidth, destY + destHeight,
            srcX.toInt(), srcY.toInt(), (srcX + scaledWidth).toInt(), (srcY + scaledHeight).toInt(),
            null
        )
        graphics.dispose()
        return dest
    }

    private fun wrapContent(src: BufferedImage, fillColor: WidgetColor): BufferedImage {
        val dest = BufferedImage(352, 275, 2)
        var destX = 16
        var destY = 16
        val destWidth = 160
        val destHeight = 259

        val diffWidth = src.width - destWidth
        val diffHeight = src.height - destHeight

        val factor = if (diffWidth < diffHeight) {
            destWidth / src.width.toDouble()
        } else {
            destHeight / src.height.toDouble()
        }

        val scaledWidth = src.width * factor
        val scaledHeight = src.height * factor
        val offsetX = (scaledWidth - destWidth) / 2.0
        val offsetY = (scaledHeight - destHeight) / 2.0

        destX -= offsetX.toInt()
        destY -= offsetY.toInt()

        val graphics = dest.graphics
        graphics.color = fillColor.base
        graphics.fillRect(0, 0, dest.width, dest.height)
        graphics.color = Color(255, 255, 255, 255)
        graphics.drawImage(src, destX, destY, scaledWidth.toInt(), scaledHeight.toInt(), null)
        graphics.dispose()
        return dest
    }
}