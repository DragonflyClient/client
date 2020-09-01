package net.inceptioncloud.dragonfly.cosmetics.types.capes

import net.inceptioncloud.dragonfly.cosmetics.logic.CosmeticsManager
import net.inceptioncloud.dragonfly.cosmetics.logic.EnumCosmeticType
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.client.renderer.IImageBuffer
import net.minecraft.client.renderer.ThreadDownloadImageData
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import org.apache.logging.log4j.LogManager
import java.awt.image.BufferedImage

object CapeManager {

    /**
     * Downloads the cape for the [player] and sets the [AbstractClientPlayer.locationOfCape]
     * after the download has finished. The url is fetched by [getCapeURL] depending on the
     * cosmetic id of the currently active cape.
     */
    @JvmStatic
    fun downloadCape(player: AbstractClientPlayer) {
        val username = player.nameClear
        val url = getCapeURL(player)

        if (url != null && username != null && username.isNotEmpty()) {
            LogManager.getLogger().debug("Downloading cape for $username...")
            val resourceLocation = ResourceLocation("capeof/${player.gameProfile.id}")
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
                    return parseCape(image)
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

    /**
     * Loads the url of the [player]'s cape by the cosmetic id of his active cape. If
     * no cape is active, this function returns null.
     */
    private fun getCapeURL(player: EntityPlayer): String? {
        val cosmetics = player.cosmetics?.filter { it.enabled }
        val databaseModels = cosmetics?.mapNotNull { CosmeticsManager.getDatabaseModelById(it.cosmeticId) }
        val cape = databaseModels?.firstOrNull { it.get("type").asString == EnumCosmeticType.CAPE.name }
        val cosmeticId = cape?.get("cosmeticId")?.asInt

        return cape?.let { "https://cdn.icnet.dev/cosmetics/capes/$cosmeticId.png" }
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
}