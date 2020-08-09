package net.inceptioncloud.dragonfly.engine.internal

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.engine.GraphicsEngine
import net.inceptioncloud.dragonfly.engine.structure.IDraw

/**
 * ## Widget Stage
 *
 * This class holds multiple widgets inside of it. The widgets can be dynamically updated during the drawing process.
 * All implemented functions are thread-safe.
 *
 * When the client screen is drawn, the [render] function is called which calls the [IDraw.drawNative] function
 * on the widgets. The [update] function updates the widgets' states (using [Widget.update]) on every mod tick.
 * This allows the client to only run the animations for the current screen.
 *
 * A widget can be added to the stage using [add] and the whole stage can be cleared with [clear].
 * You cannot directly remove a widget from the stage, but you can change it's visibility state ([Widget.isVisible]).
 *
 * @param name a name for the stage that is used in the inspector
 */
class WidgetStage(val name: String) {

    /**
     * A mutable map that contains all widgets of the stage.
     *
     * Each widget has a unique id which is the key in the map. When adding a new widget to the
     * stage with an already existing id, the widget will be overwritten.
     */
    val content = mutableMapOf<String, Widget<*>>()

    /**
     * Renders all [Widget] objects in the stage.
     *
     * This calls the [Widget.drawNative] function on all widgets whose visibility property ([Widget.isVisible]) evaluates
     * to true. If it doesn't, the draw function won't be called.
     */
    fun render() = synchronized(this) {
        content.values.toTypedArray().filter { it.isVisible }.forEach {
            it.draw()
        }

        if (Dragonfly.isDeveloperMode) {
            GraphicsEngine.renderDebugOverlay(content.filter { it.key != "background" }) // don't show debug overlay for background!
        }
    }

    /**
     * Adds a [Widget] object to the stage.
     *
     * After the widget object has been added, it will automatically be included in the render
     * and update process. To add multiple widgets, use [add].
     */
    fun add(widgetWithId: Pair<String, Widget<*>>) = synchronized(this) {
        content += widgetWithId
    }

    /**
     * Adds multiple [Widget] objects to the stage.
     *
     * @see add
     */
    fun add(vararg widgetWithId: Pair<String, Widget<*>>) = synchronized(this) {
        content += widgetWithId
    }

    /**
     * Clears the stage by removing all widgets from it.
     */
    fun clear() = synchronized(this) {
        content.clear()
    }

    /**
     * Finds a widget in the stage by searching for its id. Since every id is unique, there
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
        if (GraphicsEngine.getMouseX() != mouseX && GraphicsEngine.getMouseY() != mouseY) {
            mouseX = GraphicsEngine.getMouseX()
            mouseY = GraphicsEngine.getMouseY()
            handleMouseMove(MouseData(mouseX, mouseY))
        }

        content.values.toTypedArray().forEach { it.update() }
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
    fun handleMousePress(data: MouseData) {
        content.values.forEach { it.handleMousePress(data) }
    }

    /**
     * Called when a mouse button is released.
     */
    fun handleMouseRelease(data: MouseData) {
        content.values.forEach { it.handleMouseRelease(data) }
    }

    /**
     * Called when the mouse is moved while a button is holt down.
     */
    fun handleMouseDrag(data: MouseData) {
        content.values.forEach { it.handleMouseDrag(data) }
    }

    /**
     * Called when a key on the keyboard is typed.
     */
    fun handleKeyTyped(char: Char, keyCode: Int) {
        content.values.forEach { it.handleKeyTyped(char, keyCode) }
    }
    //</editor-fold>

    override fun toString(): String {
        val builder = StringBuilder("WidgetStage(${content.size})\n{\n")

        content.forEach {
            builder.append("\t${it.key}")
            if (!it.value.isVisible)
                builder.append(" (invisible)")
            builder.append(": ${it.value}\n")
        }

        return builder.append("}").toString()
    }
}