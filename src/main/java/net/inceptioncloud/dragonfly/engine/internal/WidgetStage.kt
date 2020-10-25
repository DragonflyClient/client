package net.inceptioncloud.dragonfly.engine.internal

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import net.inceptioncloud.dragonfly.engine.GraphicsEngine
import net.inceptioncloud.dragonfly.engine.inspector.InspectorService
import net.inceptioncloud.dragonfly.engine.inspector.InspectorService.platform
import net.inceptioncloud.dragonfly.engine.structure.IDraw
import net.inceptioncloud.dragonfly.mc
import tornadofx.*

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
    private val contentPrivate = mutableMapOf<String, Widget<*>>()

    /**
     * An immutable version of [contentPrivate] that is exposed publicly.
     */
    val content get() = contentPrivate.toMap()

    /**
     * An observable variant of the [content] as a list of pairs.
     */
    val observableContent: ObservableList<Pair<String, Widget<*>>> = FXCollections.observableArrayList()

    /**
     * Renders all [Widget] objects in the stage.
     *
     * This calls the [Widget.drawNative] function on all widgets whose visibility property ([Widget.isVisible]) evaluates
     * to true. If it doesn't, the draw function won't be called.
     */
    fun render() = synchronized(this) {
        mc.mcProfiler.startSection("stage:${name.toLowerCase().replace(" ", "-")}")
        content.values.toTypedArray()
            .sortedBy { it.stagePriority }
            .filter { it.isVisible }.forEach {
                it.draw()
            }
        mc.mcProfiler.endSection()
    }

    /**
     * Adds a [Widget] object to the stage.
     *
     * After the widget object has been added, it will automatically be included in the render
     * and update process. To add multiple widgets, use [add].
     */
    fun add(widgetWithId: Pair<String, Widget<*>>) = synchronized(this) {
        contentPrivate += widgetWithId
        widgetWithId.second.widgetId = widgetWithId.first
        widgetWithId.second.parentStage = this
        (widgetWithId.second as? AssembledWidget<*>)?.runStructureUpdate()
        inspector { observableContent += widgetWithId }
    }

    /**
     * Adds multiple [Widget] objects to the stage.
     *
     * @see add
     */
    fun add(vararg widgetWithId: Pair<String, Widget<*>>) = synchronized(this) {
        contentPrivate += widgetWithId
        widgetWithId.forEach {
            it.second.widgetId = it.first
            it.second.parentStage = this
            (it.second as? AssembledWidget<*>)?.runStructureUpdate()
        }
        inspector { observableContent += widgetWithId }
    }

    /**
     * Clears the stage by removing all widgets from it.
     */
    fun clear() = synchronized(this) {
        contentPrivate.clear()
        contentPrivate.forEach {
            it.value.widgetId = null
            it.value.parentStage = null
        }
        inspector { observableContent.clear() }
    }

    /**
     * Removes the widget with the specified [id] from the stage.
     */
    fun remove(id: String) = synchronized(this) {
        val widget = contentPrivate.remove(id)
        widget?.widgetId = null
        widget?.parentStage = null
        inspector { observableContent.remove(id to widget) }
    }

    /**
     * Removes the given [widget] from the stage.
     */
    fun remove(widget: Widget<*>) = synchronized(this) {
        contentPrivate.entries.filter { it.value == widget }
            .forEach { remove(it.key) }
    }

    /**
     * Finds a widget in the stage by searching for its id. Since every id is unique, there
     * will never be more than one result. If no widget was found, this function returns null.
     */
    operator fun get(id: String): Widget<*>? = synchronized(this) {
        return contentPrivate.getOrDefault(id, null)
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
            handleMouseMove(MouseData(mouseX.toInt(), mouseY.toInt()))
        }

        contentPrivate.values.toTypedArray()
            .sortedBy { it.stagePriority }
            .forEach { it.update() }
    }

    /**
     * Calls the given [block] on the JavaFx platform if the inspector is launched or in the
     * current thread if it isn't.
     */
    private fun inspector(block: () -> Unit) {
        if (InspectorService.isLaunched() == true) {
            platform(block)
        } else {
            block()
        }
    }

    //<editor-fold desc="Mouse Events">
    var mouseX: Float = 0.0f
    var mouseY: Float = 0.0f

    /**
     * Called when the mouse was moved.
     */
    private fun handleMouseMove(data: MouseData) = Defaults.handleMouseMove(
        contentPrivate.values.sortedBy { it.stagePriority }, data
    )

    /**
     * Called when a mouse button is pressed.
     */
    fun handleMousePress(data: MouseData) {
        contentPrivate.values
            .sortedBy { it.stagePriority }
            .forEach { it.handleMousePress(data) }
    }

    /**
     * Called when a mouse button is released.
     */
    fun handleMouseRelease(data: MouseData) {
        contentPrivate.values
            .sortedBy { it.stagePriority }
            .forEach { it.handleMouseRelease(data) }
    }

    /**
     * Called when the mouse is moved while a button is holt down.
     */
    fun handleMouseDrag(data: MouseData) {
        contentPrivate.values
            .sortedBy { it.stagePriority }
            .forEach { it.handleMouseDrag(data) }
    }

    /**
     * Called when a key on the keyboard is typed.
     */
    fun handleKeyTyped(char: Char, keyCode: Int) {
        contentPrivate.values
            .sortedBy { it.stagePriority }
            .forEach { it.handleKeyTyped(char, keyCode) }
    }
    //</editor-fold>
}