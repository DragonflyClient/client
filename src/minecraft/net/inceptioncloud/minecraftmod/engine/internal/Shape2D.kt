package net.inceptioncloud.minecraftmod.engine.internal

import net.inceptioncloud.minecraftmod.engine.structure.IColorable
import net.inceptioncloud.minecraftmod.engine.structure.IDimension
import net.inceptioncloud.minecraftmod.engine.structure.IDrawable
import net.inceptioncloud.minecraftmod.engine.structure.IPosition
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties

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
abstract class Shape2D<T : Any> : IPosition, IDimension, IDrawable, IColorable
{
    /**
     * Whether the shape is currently visible.
     *
     * If this flag is set to false, the [draw] method won't be called by the
     * parent [ShapeBuffer2D] that contains the shape.
     */
    var visible = true

    /**
     * A simple method that uses the shape as a receiver in order to allow changes to it during lifetime.
     *
     * This will be invoked on every tick. It is called on a clone of the current object to which the
     * changes will be made. After the execution, the new object will be compared to the original one
     * and the changes will be applied to the original one.
     */
    private var dynamicUpdate: (T.() -> Unit)? = null

    /**
     * Sets the values of the shape statically without dynamic updating.
     *
     * Note, that this doesn't remove the [dynamicUpdate] function so it doesn't prevent it.
     * It only sets the values for the moment but they can be updated by a future update.
     */
    fun static(x: Double, y: Double, width: Double, height: Double, color: Color2D): T
    {
        this.x = x
        this.y = y
        this.width = width
        this.height = height
        this.color = color
        @Suppress("UNCHECKED_CAST")
        return this as T
    }

    /**
     * Sets the function to dynamically update the shape.
     *
     * This function is called on every shape update by the buffer (on every mod tick) in order to
     * make changes on a cloned version of the shape that will then be merged onto the original shape.
     */
    fun dynamic(dynamicUpdate: T.() -> Unit): T
    {
        this.dynamicUpdate = dynamicUpdate
        @Suppress("UNCHECKED_CAST")
        return this as T
    }

    /**
     * The update function is called on every mod tick (regardless of the visibility state).
     *
     * It performs things like state- and dynamic updates and allows the use of animations.
     */
    open fun update()
    {
        if (dynamicUpdate != null)
        {
            val clone = clone()
            dynamicUpdate?.invoke(clone)
            mergeChangesFromClone(clone)
        }
    }

    /**
     * Clones the graphics object.
     *
     * @return an identical copy of the object that the function was called on
     */
    abstract fun clone(): T

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
    abstract fun cloneWithPadding(padding: Double): T

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
    abstract fun cloneWithMargin(margin: Double): T

    /**
     * Merges changes from the clone to the instance's values.
     *
     * Iterates over all member properties that are annotated with [@Dynamic][Dynamic] and compares their
     * values to the ones in the cloned object. If they don't match, it will set the property on the current
     * instance to the value of the cloned instance.
     *
     * @param clone the cloned instance from which the changes are merged
     */
    private fun mergeChangesFromClone(clone: T)
    {
        this::class.memberProperties
            .filter { it.hasAnnotation<Dynamic>() && it is KMutableProperty<*> }
            .forEach { property ->
                val originalProperty = property as KMutableProperty<*>
                val originalValue = originalProperty.getter.call(this@Shape2D)

                val cloneProperty = clone::class.memberProperties.find { it.name == property.name } as KMutableProperty<*>
                val cloneValue = cloneProperty.getter.call(clone)

                if (originalValue != cloneValue)
                {
                    originalProperty.setter.call(this@Shape2D, cloneValue!!)
                    println("${property.name}: $originalValue -> $cloneValue")
                }
            }
    }
}