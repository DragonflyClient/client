package net.inceptioncloud.minecraftmod.engine.font

data class FontRendererBuilder(
    var fontWeight: FontWeight,
    var size: Int,
    var letterSpacing: Double,
    var forceCreation: Boolean = false
)