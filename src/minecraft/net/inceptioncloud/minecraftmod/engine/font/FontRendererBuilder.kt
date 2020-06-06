package net.inceptioncloud.minecraftmod.engine.font

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
) {
    /**
     * Default equals() implementation to compare builder preferences.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FontRendererBuilder

        if (fontWeight != other.fontWeight) return false
        if (size != other.size) return false
        if (letterSpacing != other.letterSpacing) return false
        if (forceCreation != other.forceCreation) return false

        return true
    }

    /**
     * Default hashCode() implementation
     */
    override fun hashCode(): Int {
        var result = fontWeight.hashCode()
        result = 31 * result + size
        result = 31 * result + letterSpacing.hashCode()
        result = 31 * result + forceCreation.hashCode()
        return result
    }
}