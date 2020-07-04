package net.inceptioncloud.dragonfly.engine.internal

import net.inceptioncloud.dragonfly.engine.structure.*

/**
 * Specifies the alignment of the widget.
 *
 * This would only work if the widget implements the [IPosition] and one of both [IDimension]
 * and [ISize] interfaces.
 *
 * The alignment can be used both horizontally and vertically using the [calc] function.
 */
enum class Alignment
(
    /**
     * Calculates the x or y coordinate based on the input position and input size.
     * The output of this function will be the resulting x/y coordinate.
     */
    private val calc: (coordinate: Double, size: Double) -> Double,

    /**
     * A mathematic function to reverse the calculation that was done by the [calc] function.
     */
    private val reverse: (coordinate: Double, size: Double) -> Double
)
{
    /**
     * Keeps the input location of the widget. In this case, the widget is drawn on the right/bottom
     * side of the position.
     *
     * - Horizontally = **Left**
     * - Vertically = **Top**
     */
    START(
        { coordinate, _ -> coordinate },
        { coordinate, _ -> coordinate }
    ),

    /**
     * Subtracts the width/height to the input location of the widget. In this case, the widget is
     * drawn on the left/top side of the position.
     *
     * - Horizontally = **Right**
     * - Vertically = **Bottom**
     */
    END(
        { coordinate, size -> coordinate - size },
        { coordinate, size -> coordinate + size }
    ),

    /**
     * Subtracts half the width/height to the input location of the widget. In this case, the widget's
     * center is located on the position.
     *
     * - Horizontally = **Center**
     * - Vertically = **Center**
     */
    CENTER(
        { coordinate, size -> coordinate - size / 2 },
        { coordinate, size -> coordinate + size / 2 }
    );

    /**
     * Invokes the [calc] function.
     */
    fun calc(coordinate: Double, size: Double) = calc.invoke(coordinate, size)

    /**
     * Invokes the [reverse] function.
     */
    fun reverse(coordinate: Double, size: Double) = reverse.invoke(coordinate, size)
}