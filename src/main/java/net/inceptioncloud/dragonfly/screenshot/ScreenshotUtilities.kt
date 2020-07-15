package net.inceptioncloud.dragonfly.screenshot

import net.inceptioncloud.dragonfly.options.sections.OptionsSectionClient
import java.awt.image.BufferedImage
import java.io.File

object ScreenshotUtilities {

    val isUtilitiesEnabled: Boolean
        get() = OptionsSectionClient.screenshotUtilities.getKey().get()

    @JvmStatic
    fun screenshotTaken(image: BufferedImage, file: File): Boolean {
        if (!isUtilitiesEnabled)
            return false

        return true
    }
}