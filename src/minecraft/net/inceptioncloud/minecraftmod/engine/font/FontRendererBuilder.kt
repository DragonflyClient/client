package net.inceptioncloud.minecraftmod.engine.font

/**
 * Preferences that can be applied when building a font renderer.
 */
data class FontRendererBuilder(
    var fontWeight: FontWeight = FontWeight.REGULAR,
    var size: Int = 19,
    var letterSpacing: Double,
    var forceCreation: Boolean = false
)