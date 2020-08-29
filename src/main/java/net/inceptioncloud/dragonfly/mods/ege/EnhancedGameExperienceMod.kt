package net.inceptioncloud.dragonfly.mods.ege

import net.inceptioncloud.dragonfly.mods.core.DragonflyMod
import org.apache.logging.log4j.LogManager
import java.awt.image.BufferedImage

object EnhancedGameExperienceMod : DragonflyMod("Enhanced Game Experience") {

    @JvmStatic
    var tcpNoDelay by option(true)

    @JvmStatic
    var scaleResourcePackIcons by option(true)
    var maximumPackIconSize by option(64)

    var disableExplicitGC by option(false)

    @JvmStatic
    var fixPlayerRotation by option(true)

    @JvmStatic
    fun tryExplicitGC() {
        if (!disableExplicitGC) System.gc()
    }

    @JvmStatic
    fun scalePackImage(image: BufferedImage?): BufferedImage? {
        if (image == null) return null
        if (image.width <= maximumPackIconSize) return image

        LogManager.getLogger().info("Scaling resource pack icon from ${image.width}x to ${maximumPackIconSize}x")

        val smallImage = BufferedImage(maximumPackIconSize, maximumPackIconSize, BufferedImage.TYPE_INT_ARGB)
        val graphics = smallImage.graphics

        graphics.drawImage(image, 0, 0, maximumPackIconSize, maximumPackIconSize, null)
        graphics.dispose()

        return smallImage
    }
}