package net.inceptioncloud.dragonfly.design.color

import java.awt.Color

/**
 * A blue palette for the client design.
 */
object BluePalette {
    /**
     * For writing text on either the background or the primary color.
     */
    @JvmStatic
    val FOREGROUND = Color(0xEFEFEF)

    /**
     * For the UI background.
     */
    @JvmStatic
    val BACKGROUND = Color(0x1C3E5B)

    /**
     * Darker accent of the primary color.
     */
    @JvmStatic
    val PRIMARY_DARK = Color(0x027BCE)

    /**
     * The primary blue color.
     */
    @JvmStatic
    val PRIMARY = Color(0x0496FF)

    /**
     * Lighter accent of the primary color.
     */
    @JvmStatic
    val PRIMARY_LIGHT = Color(0x4BB3FD)
}