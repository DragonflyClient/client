package net.inceptioncloud.dragonfly.engine.font

/**
 * ## Font Renderer Builder
 *
 * Specify preferences for building a font renderer based on a [WidgetFont]
 *
 * @param fontWeight the font weight ([FontWeight.REGULAR] by default)
 * @param size the size of the font (19 by default)
 * @param letterSpacing optional modification of the letter spacing (0.0 by default)
 * @param forceCreation true, if no cached font renderer should be used
 */
data class FontRendererBuilder(
    var fontWeight: FontWeight,
    var size: Int,
    var letterSpacing: Double,
    var forceCreation: Boolean = false
)