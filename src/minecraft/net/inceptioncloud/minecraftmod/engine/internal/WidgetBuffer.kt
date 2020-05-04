package net.inceptioncloud.minecraftmod.engine.internal

import net.inceptioncloud.minecraftmod.engine.structure.IDrawable

/**
 * ## Widget Buffer Class
 *
 * This class holds multiple widgets inside of it. The widgets can be dynamically updated during the drawing process.
 * All implemented functions are thread-safe.
 *
 * When the client screen is drawn, the [render] function is called which calls the [IDrawable.drawNative] function
 * on the widgets. The [update] function updates the widgets' states (using [Widget.update]) on every mod tick.
 * This allows the client to only run the animations for the current screen.
 *
 * A widget can be added to the buffer using [add] and the whole buffer can be cleared with [clear].
 * You cannot directly remove a widget from the buffer, but you can change it's visibility state ([Widget.visible]).
 */
class WidgetBuffer
{
    /**
     * A mutable list that contains all widgets of the buffer.
     */
    private val content = mutableListOf<Widget<*>>()

    /**
     * Adds a [Widget] object to the buffer.
     *
     * After the widget object has been added, it will automatically be included in the render
     * and update process. To add multiple widgets, use [add].
     */
    fun add(shape: Widget<*>)
    {
        synchronized(this) { content.add(shape) }
    }

    /**
     * Adds multiple [Widget] objects to the buffer.
     *
     * @see add
     */
    fun add(vararg shape: Widget<*>)
    {
        synchronized(this) { content.addAll(shape) }
    }

    /**
     * Clears the buffer by removing all widgets from it.
     */
    fun clear()
    {
        synchronized(this) { content.clear() }
    }

    /**
     * Renders all [Widget] objects in the buffer.
     *
     * This calls the [Widget.drawNative] function on all widgets whose visibility property ([Widget.visible]) evaluates
     * to true. If it doesn't, the draw function won't be called.
     */
    fun render()
    {
        synchronized(this) {
            content.filter { it.visible }.forEach {
                it.draw()
            }
        }
    }

    /**
     * Updates the content on mod tick.
     *
     * Calls the [Widget.update] function on every widget regardless of the visibility state.
     */
    fun update()
    {
        synchronized(this) { content.forEach { it.update() } }
    }
}