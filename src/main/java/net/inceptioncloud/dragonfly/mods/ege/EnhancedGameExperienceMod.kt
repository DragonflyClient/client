package net.inceptioncloud.dragonfly.mods.ege

import net.inceptioncloud.dragonfly.controls.*
import net.inceptioncloud.dragonfly.mods.core.DragonflyMod
import org.apache.logging.log4j.LogManager
import java.awt.image.BufferedImage

object EnhancedGameExperienceMod : DragonflyMod("Enhanced Game Experience") {

    // enhancements
    @JvmStatic var tcpNoDelay by option(true)
    @JvmStatic var scaleResourcePackIcons by option(true)
    var maximumPackIconSize by option(64) { it in 1..128 }
    var disableExplicitGC by option(false)

    // bug fixes
    @JvmStatic var fixPlayerRotation by option(true)
    @JvmStatic var fixRidingHand by option(true)

    /**
     * Calls [System.gc] if it is not [disabled][disableExplicitGC].
     */
    @JvmStatic
    fun tryExplicitGC() {
        if (!disableExplicitGC) System.gc()
    }

    /**
     * Scales the given [resource pack icon][icon] to meet the [maximumPackIconSize].
     */
    @JvmStatic
    fun scalePackIcon(icon: BufferedImage?): BufferedImage? {
        if (icon == null) return null
        if (icon.width <= maximumPackIconSize) return icon

        LogManager.getLogger().info("Scaling resource pack icon from ${icon.width}x to ${maximumPackIconSize}x")

        val smallImage = BufferedImage(maximumPackIconSize, maximumPackIconSize, BufferedImage.TYPE_INT_ARGB)
        val graphics = smallImage.graphics

        graphics.drawImage(icon, 0, 0, maximumPackIconSize, maximumPackIconSize, null)
        graphics.dispose()

        return smallImage
    }

    override fun publishControls() = listOf(
        TitleControl("Enhancements",
            "Customize enhancements that have been made to the Minecraft game in order to improve your game experience"),
        BooleanControl(::tcpNoDelay, "TCP no delay", "Improve your network connection speed by enabling this option"),
        BooleanControl(::scaleResourcePackIcons, "Scale resource pack icons",
            "Decreases the size of resource pack icons to increase your performance and free memory"),
        NumberControl(::maximumPackIconSize, "Maximum pack icon size",
            "The maximum size that a resource pack icon is allowed to have, icons above this size are scaled down", min = 1.0, max = 128.0,
            decimalPlaces = 0, formatter = { "${it}x$it" }),
        BooleanControl(::disableExplicitGC, "Disable explicit garbage collection",
            "Prevents Minecraft from explicitly calling the JVM garbage collector to decrease world load time"),
        TitleControl("Bug fixes", "Some issues that exist in Minecraft have been fixed by Dragonfly"),
        BooleanControl(::fixPlayerRotation, "Fix player rotation (crosshair sync)",
            "Synchronizes the player look direction with their actual rotation and crosshair position which enhances the pvp experience"),
        BooleanControl(::fixRidingHand, "Fix hand while riding", "Fixes the right hand of the player which can look weird when the player is riding")
    )
}