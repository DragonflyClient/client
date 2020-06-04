package net.inceptioncloud.minecraftmod.engine.font

data class FontRendererBuilder(
    var fontWeight: FontWeight,
    var size: Int,
    var letterSpacing: Double,
    var forceCreation: Boolean = false
) {
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

    override fun hashCode(): Int {
        var result = fontWeight.hashCode()
        result = 31 * result + size
        result = 31 * result + letterSpacing.hashCode()
        result = 31 * result + forceCreation.hashCode()
        return result
    }
}