package net.inceptioncloud.dragonfly.engine.internal

import net.inceptioncloud.dragonfly.engine.structure.*

/**
 * ## Defaults
 *
 * Contains default methods that can be applied on a wide range of widgets
 * that should simplify the widget's code.
 */
object Defaults {
    /**
     * Changes the width and the height of the widget based on its structure.
     *
     * - If the widget is based on a [size][ISize], the size will be set to the width.
     * - If the widget is based on a [dimension][IDimension], the width and height will be set to the parameters.
     */
    fun <Type : Widget<Type>> setSizeOrDimension(widget: Type, width: Double, height: Double): Type
    {
        when (widget)
        {
            is IDimension ->
            {
                widget.width = width
                widget.height = height
            }
            is ISize ->
            {
                require(width == height)
                widget.size = width
            }
            else -> throw IllegalArgumentException("Cannot set size or dimension for widget $widget")
        }

        return widget
    }

    /**
     * Retrieves the width and the height of the widget based on its structure. It returns a pair
     * whose first component is the width and whose second component is the height.
     *
     * - If the widget is based on a [size][ISize], the size will be returned for both the width and the height.
     * - If the widget is based on a [dimension][IDimension]. the width and height will be returned.
     */
    fun getSizeOrDimension(widget: Widget<*>): Pair<Double, Double> = when (widget) {
        is IDimension -> widget.width to widget.height
        is ISize -> widget.size to widget.size
        else -> throw IllegalArgumentException("Cannot get size or dimension for widget $widget")
    }

    /**
     * A default implementation for the mouse move event.
     */
    fun handleMouseMove(widgets: Collection<Widget<*>>, data: MouseData) {
        widgets.forEach { it.handleMouseMove(data) }
    }
}