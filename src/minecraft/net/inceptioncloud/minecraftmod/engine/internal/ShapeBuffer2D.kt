package net.inceptioncloud.minecraftmod.engine.internal

import net.inceptioncloud.minecraftmod.engine.structure.IDrawable

/**
 * ## Shape Buffer Class
 *
 * This class holds multiple shapes inside of it. The shapes can be dynamically updated during the drawing process.
 * All implemented functions are thread-safe.
 *
 * When the client screen is drawn, the [renderBuffer] function is called which calls the [IDrawable.draw] function
 * on the shapes. The [updateBuffer] function updates the shapes' states (using [Shape2D.update]) on every mod tick.
 * This allows the client to only run the animations for the current screen.
 *
 * A shape can be added to the buffer using [addShape] and the whole buffer can be cleared with [clearBuffer].
 * You cannot directly remove a shape from the buffer, but you can change it's visibility state ([Shape2D.visible]).
 */
class ShapeBuffer2D
{
    /**
     * A mutable list that contains all shapes of the buffer.
     */
    private val content = mutableListOf<Shape2D<*>>()

    /**
     * Adds a [Shape2D] object to the buffer.
     *
     * After the shape object has been added, it will automatically be included in the render
     * and update process. To add multiple shapes, use [addShapes].
     */
    fun addShape(shape: Shape2D<*>)
    {
        synchronized(this) { content.add(shape) }
    }

    /**
     * Adds multiple [Shape2D] objects to the buffer.
     *
     * @see addShape
     */
    fun addShapes(vararg shape: Shape2D<*>)
    {
        synchronized(this) { content.addAll(shape) }
    }

    /**
     * Clears the buffer by removing all shapes from it.
     */
    fun clearBuffer()
    {
        synchronized(this) { content.clear() }
    }

    /**
     * Renders all [Shape2D] objects in the buffer.
     *
     * This calls the [Shape2D.draw] function on all shapes whose visibility property ([Shape2D.visible]) evaluates
     * to true. If it doesn't, the draw function won't be called.
     */
    fun renderBuffer()
    {
        synchronized(this) { content.filter { it.visible }.forEach { it.draw() } }
    }

    /**
     * Updates the content on mod tick.
     *
     * Calls the [Shape2D.update] function on every shape regardless of the visibility state.
     */
    fun updateBuffer()
    {
        synchronized(this) { content.forEach { it.update() } }
    }
}