package net.inceptioncloud.minecraftmod.engine.structure

import net.inceptioncloud.minecraftmod.engine.internal.Alignment

/**
 * ## Align Interface
 *
 * Specifies that the parent widget can be aligned by the [Alignment] enum.
 */
interface IAlign
{
    /**
     * The horizontal alignment for the x-axis.
     */
    var horizontalAlignment: Alignment

    /**
     * The vertical alignment for the y-axis.
     */
    var verticalAlignment: Alignment

    /**
     * Applies the alignment on the parent's position.
     */
    fun align(x: Double, y: Double, width: Double, height: Double)
}