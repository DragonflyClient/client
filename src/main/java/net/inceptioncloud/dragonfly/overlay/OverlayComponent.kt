package net.inceptioncloud.dragonfly.overlay

import net.inceptioncloud.dragonfly.engine.internal.Widget
import net.inceptioncloud.dragonfly.engine.internal.WidgetIdBuilder

/**
 * Represents a single component that can be added to the overlay and will be drawn
 * on it.
 *
 * @param name the name of the component
 */
abstract class OverlayComponent<W : Widget<W>>(
    val name: String
) : Widget<W>() {

    /**
     * A collection of all child widgets used on the overlay component.
     */
    private val widgets = mutableMapOf<String, Widget<*>>()

    /**
     * Initialize the component by adding it's child widgets to the overlay buffer.
     *
     * This is called when the component is added to the overlay or when the screen
     * is resized in order to adapt the changes.
     */
    abstract fun initialize(width: Double, height: Double)

    /**
     * Tries to get a widget and additionally cast it to the specified type. This will return
     * null if the widget was not found or cannot be cast.
     */
    @Suppress("UNCHECKED_CAST")
    fun <W : Widget<W>> getWidget(identifier: String): W? = widgets[identifier] as? W

    /**
     * Updates the widget found by the [identifier] (via [getWidget]) and applies the given
     * [block] to it.
     */
    fun <W : Widget<W>> updateWidget(identifier: String, block: W.() -> Unit): W? = getWidget<W>(identifier)?.apply(block)

    /**
     * An operator function that allows adding widgets to the buffer. After providing the widget,
     * an id for it must be specified with the infix function [WidgetIdBuilder.id].
     */
    operator fun <W : Widget<W>> W.unaryPlus(): WidgetIdBuilder<W> {
        return WidgetIdBuilder(this) { id, widget -> widgets[id] = widget }
    }
}