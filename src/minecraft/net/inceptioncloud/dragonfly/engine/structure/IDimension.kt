package net.inceptioncloud.dragonfly.engine.structure

/**
 * ## Dimension Interface
 *
 * The Dimension Interface lets the inheriting class define a value for the width and height.
 * Every object that has a width and height should implement this interface.
 */
interface IDimension
{
    /**
     * The width of the object.
     *
     * It specifies the size across the horizontal x axis starting from the left to the right.
     *
     * @see height
     */
    var width: Double

    /**
     * The height of the object.
     *
     * It specifies the size across the vertical y axis starting from the top to the bottom.
     *
     * @see width
     */
    var height: Double
}