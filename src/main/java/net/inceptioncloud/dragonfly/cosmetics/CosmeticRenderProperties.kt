package net.inceptioncloud.dragonfly.cosmetics

/**
 * Holds additional information that can be consumed by cosmetics
 * during their rendering process.
 */
data class CosmeticRenderProperties(
    val limbSwing: Float,
    val limbSwingAmount: Float,
    val ageInTicks: Float,
    val headYaw: Float,
    val headPitch: Float,
    val scale: Float,
    val partialTicks: Float
)