package net.inceptioncloud.minecraftmod.engine.structure

import net.inceptioncloud.minecraftmod.engine.internal.Color2D

/**
 * ## Colorable Interface
 *
 * By implementing this interface, the graphics object can receive a color that will
 * be applied during the drawing process.
 */
interface IColorable
{
    /**
     * The color of the object represented by a [Color2D] wrapper.
     */
    var color: Color2D
}