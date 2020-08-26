package net.inceptioncloud.dragonfly.engine.structure

import net.inceptioncloud.dragonfly.engine.internal.WidgetColor
import net.inceptioncloud.dragonfly.engine.internal.annotations.Interpolate

/**
 * ## Colorable Interface
 *
 * By implementing this interface, the graphics object can receive a color that will
 * be applied during the drawing process.
 */
interface IColor {
    /**
     * The color of the object represented by a [WidgetColor] wrapper.
     */
    @Interpolate var color: WidgetColor
}