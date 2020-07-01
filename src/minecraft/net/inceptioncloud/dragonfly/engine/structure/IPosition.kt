package net.inceptioncloud.dragonfly.engine.structure

/**
 * ## Position Interface
 *
 * The Position Interface lets the inheriting class define a value for the x and y position.
 * Every object that has a position should implement this interface.
 */
interface IPosition
{
    /**
     * The x-position of the object on the screen.
     *
     * The x axis is the horizontal axis that increases from the left to the right.
     * This point is part the location of the top-left corner of the object.
     *
     * @see y
     */
    var x: Double

    /**
     * The y-position of the object on the screen.
     *
     * The y axis is the vertical axis that increases from the top to the bottom.
     * This point is part the location of the top-left corner of the object.
     *
     * @see x
     */
    var y: Double
}