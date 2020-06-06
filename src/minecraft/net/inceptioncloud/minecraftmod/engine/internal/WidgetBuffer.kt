package net.inceptioncloud.minecraftmod.engine.internal

import net.inceptioncloud.minecraftmod.Dragonfly
import net.inceptioncloud.minecraftmod.engine.GraphicsEngine
import net.inceptioncloud.minecraftmod.engine.structure.IDraw
import org.lwjgl.input.Keyboard

/**
 * ## Widget Buffer
 *
 * This class holds multiple widgets inside of it. The widgets can be dynamically updated during the drawing process.
 * All implemented functions are thread-safe.
 *
 * When the client screen is drawn, the [render] function is called which calls the [IDraw.drawNative] function
 * on the widgets. The [update] function updates the widgets' states (using [Widget.update]) on every mod tick.
 * This allows the client to only run the animations for the current screen.
 *
 * A widget can be added to the buffer using [add] and the whole buffer can be cleared with [clear].
 * You cannot directly remove a widget from the buffer, but you can change it's visibility state ([Widget.visible]).
 */
class WidgetBuffer
{
    /**
     * A mutable map that contains all widgets of the buffer.
     *
     * Each buffer has a unique id which is the key in the map. When adding a new widget to the
     * buffer with an already existing id, the widget will be overwritten.
     */
    private val content = mutableMapOf<String, Widget<*>>()

    /**
     * Renders all [Widget] objects in the buffer.
     *
     * This calls the [Widget.drawNative] function on all widgets whose visibility property ([Widget.visible]) evaluates
     * to true. If it doesn't, the draw function won't be called.
     */
    fun render() = synchronized(this) {
        content.values.filter { it.visible }.forEach {
            it.draw()
        }

        if (Dragonfly.isDeveloperMode && !Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
            GraphicsEngine.renderDebugOverlay(content)
        }
    }

    /**
     * Adds a [Widget] object to the buffer.
     *
     * After the widget object has been added, it will automatically be included in the render
     * and update process. To add multiple widgets, use [add].
     */
    fun add(widgetWithId: Pair<String, Widget<*>>) = synchronized(this) {
        content += widgetWithId
    }

    /**
     * Adds multiple [Widget] objects to the buffer.
     *
     * @see add
     */
    fun add(vararg widgetWithId: Pair<String, Widget<*>>) = synchronized(this) {
        content += widgetWithId
    }

    /**
     * Clears the buffer by removing all widgets from it.
     */
    fun clear() = synchronized(this) {
        content.clear()
    }

    /**
     * Finds a widget in the buffer by searching for its id. Since every id is unique, there
     * will never be more than one result. If no widget was found, this function returns null.
     */
    operator fun get(id: String): Widget<*>? = synchronized(this) {
        return content.getOrDefault(id, null)
    }

    /**
     * Updates the content on mod tick.
     *
     * Calls the [Widget.update] function on every widget regardless of the visibility state.
     */
    fun update() = synchronized(this) {
        if (GraphicsEngine.getMouseX() != mouseX && GraphicsEngine.getMouseY() != mouseY)
        {
            mouseX = GraphicsEngine.getMouseX()
            mouseY = GraphicsEngine.getMouseY()
            handleMouseMove(MouseData(mouseX, mouseY))
        }

        content.values.forEach { it.update() }
    }

    //<editor-fold desc="Mouse Events">
    var mouseX: Int = 0
    var mouseY: Int = 0

    /**
     * Called when the mouse was moved.
     */
    private fun handleMouseMove(data: MouseData) = Defaults.handleMouseMove(content.values, data)

    /**
     * Called when a mouse button is pressed.
     */
    fun handleMousePress(data: MouseData)
    {
        content.values.forEach { it.handleMousePress(data) }
    }

    /**
     * Called when a mouse button is released.
     */
    fun handleMouseRelease(data: MouseData)
    {
        content.values.forEach { it.handleMouseRelease(data) }
    }

    /**
     * Called when the mouse is moved while a button is holt down.
     */
    fun handleMouseDrag(data: MouseData)
    {
        content.values.forEach { it.handleMouseDrag(data) }
    }
    //</editor-fold>

    override fun toString(): String
    {
        val builder = StringBuilder("WidgetBuffer(${content.size})\n{\n")

        content.forEach {
            builder.append("\t${it.key}")
            if (!it.value.visible)
                builder.append(" (invisible)")
            builder.append(": ${it.value}\n")
        }

        return builder.append("}").toString()
    }
}