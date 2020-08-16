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
    val FOREGROUND = DragonflyPalette.foreground.base

    /**
     * For the UI background.
     */
    @JvmStatic
    val BACKGROUND = DragonflyPalette.background.base

    /**
     * Darker accent of the primary color.
     */
    @JvmStatic
    val PRIMARY_DARK = DragonflyPalette.accentDark.base

    /**
     * The primary blue color.
     */
    @JvmStatic
    val PRIMARY = DragonflyPalette.accentNormal.base

    /**
     * Lighter accent of the primary color.
     */
    @JvmStatic
    val PRIMARY_LIGHT = DragonflyPalette.accentBright.base
}