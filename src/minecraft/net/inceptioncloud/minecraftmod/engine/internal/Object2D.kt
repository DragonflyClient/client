package net.inceptioncloud.minecraftmod.engine.internal

import net.inceptioncloud.minecraftmod.engine.structure.IColorable
import net.inceptioncloud.minecraftmod.engine.structure.IDimension
import net.inceptioncloud.minecraftmod.engine.structure.IDrawable
import net.inceptioncloud.minecraftmod.engine.structure.IPosition

/**
 * ## Object2D Interface
 *
 * A two-dimensional object is a drawable shape that has a position (x and y), a size (width and height)
 * and can receive a color. This interface provides specific methods that every 2D-object has to implement
 * in order to make drawing easier.
 *
 * Every class that implements this interface must specify its type with the type parameter `T`
 * in the interface. This allows it to return it's instance without forcing the user to use casts.
 *
 * @see IDrawable
 * @see IPosition
 * @see IDimension
 *
 * @property T the type of the implementing class
 */
interface Object2D<T> : IPosition, IDimension, IDrawable, IColorable
{
    /**
     * Clones the graphics object.
     *
     * @return an identical copy of the object that the function was called on
     */
    fun clone(): T

    /**
     * Clones the graphics object and adds a padding.
     *
     * By adding a padding, the cloned object will get smaller. It stays in the center of the original
     * object, and the distance between the outline of the cloned object and the outline of the original
     * object is equal to the padding size.
     *
     * The calculation works like this:
     * ```
     * x += padding/2
     * y += padding/2
     * width -= padding
     * height -= padding
     *
     * ._______________.
     * | .___________. |
     * | |           | |
     * | |  =cloned  | |  =original
     * | |___________| |
     * |_______________|
     * ```
     *
     * @see cloneWithMargin
     * @return a congruent copy of the object with the given padding to the original object
     */
    fun cloneWithPadding(padding: Double): T

    /**
     * Clones the graphics object and adds a margin.
     *
     * By adding a padding, the cloned object will get greater. It stays in the center of the original
     * object, and the distance between the outline of the original object and the outline of the cloned
     * object is equal to the padding size.
     * ```
     * x -= margin/2
     * y -= margin/2
     * width += margin
     * height += margin
     *
     * ._______________.
     * | .___________. |
     * | |           | |
     * | | =original | |  =cloned
     * | |___________| |
     * |_______________|
     * ```
     *
     * @see cloneWithPadding
     * @return a congruent copy of the object with the given margin to the original object
     */
    fun cloneWithMargin(margin: Double): T
}