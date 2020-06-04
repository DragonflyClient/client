package net.inceptioncloud.minecraftmod.engine.structure

import net.inceptioncloud.minecraftmod.engine.internal.WidgetColor

/**
 * ## Outline Interface
 *
 * Determines that the parent widget can have an outline and allows specifying its
 * color and width.
 */
interface IOutline
{
    /**
     * The color that the outline is rendered in.
     */
    var outlineColor: WidgetColor

    /**
     * The width (stroke) of the outline.
     */
    var outlineStroke: Double
}