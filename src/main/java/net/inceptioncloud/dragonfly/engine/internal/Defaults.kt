package net.inceptioncloud.dragonfly.engine.internal

import net.inceptioncloud.dragonfly.engine.structure.*

/**
 * ## Defaults
 *
 * Contains default methods that can be applied on a wide range of widgets
 * that should simplify the widget's code.
 */
object Defaults
{
    /**
     * Uses the default method to clone the widget and applies a padding.
     *
     * This method can handle both widget based on [dimensions][IDimension] and [sizes][ISize]
     * and pays attention to the alignment of the object.
     */
    fun <Type : Widget<Type>> cloneWithPadding(widget: Type, amount: Double): Type
    {
        val clone = widget.clone()
        clone as IPosition

        var (width, height) = getSizeOrDimension(widget)

        width -= amount * 2
        height -= amount * 2

        if (clone is IAlign)
        {
            clone.align(
                clone.horizontalAlignment.reverse(clone.x + amount, width),
                clone.verticalAlignment.reverse(clone.y + amount, height),
                width, height
            )
        } else
        {
            clone.x += amount
            clone.y += amount
            setSizeOrDimension(widget, width, height)
        }

        return clone
    }

    /**
     * Uses the default method to clone the widget and applies a margin.
     *
     * This method can handle both widget based on [dimensions][IDimension] and [sizes][ISize]
     * and pays attention to the alignment of the object.
     */
    fun <Type : Widget<Type>> cloneWithMargin(widget: Type, amount: Double): Type
    {
        val clone = widget.clone()
        clone as IPosition

        var (width, height) = getSizeOrDimension(widget)

        width += amount * 2
        height += amount * 2

        if (clone is IAlign)
        {
            clone.align(
                clone.horizontalAlignment.reverse(clone.x - amount, width),
                clone.verticalAlignment.reverse(clone.y - amount, height),
                width, height
            )
        } else
        {
            clone.x -= amount
            clone.y -= amount
            setSizeOrDimension(widget, width, height)
        }

        return clone
    }

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
        widgets.filter { it is IPosition && (it is IDimension || it is ISize) }
            .forEach {
                it as IPosition
                val x = it.x
                val y = it.y
                val (width, height) = getSizeOrDimension(it)

                it.isHovered = data.mouseX.toDouble() in x..x + width
                        && data.mouseY.toDouble() in y..y + height
            }
    }
}