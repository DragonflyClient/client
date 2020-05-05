package net.inceptioncloud.minecraftmod.engine.internal

import net.inceptioncloud.minecraftmod.engine.structure.IDimension
import net.inceptioncloud.minecraftmod.engine.structure.IPosition
import net.inceptioncloud.minecraftmod.engine.structure.ISize

/**
 * Specifies the alignment of the widget.
 *
 * This would only work if the widget implements the [IPosition] and one of both [IDimension]
 * and [ISize] interfaces.
 *
 * The alignment can be used both horizontally (by using the [calcHorizontal] function) and
 * vertically (by using the [calcVertical] function).
 */
enum class Alignment
(
    /**
     * Calculates the x coordinate based on the input position and input width if the alignment
     * is used horizontally. The output of this function will be the resulting x coordinate.
     */
    private val calcHorizontal: (x: Double, width: Double) -> Double,

    /**
     * Calculates the y coordinate based on the input position and input height if the alignment
     * is used vertically. The output of this function will be the resulting y coordinate.
     */
    private val calcVertical: (y: Double, height: Double) -> Double
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
        { x, _ -> x },
        { y, _ -> y }
    ),

    /**
     * Subtracts the width/height to the input location of the widget. In this case, the widget is
     * drawn on the left/top side of the position.
     *
     * - Horizontally = **Right**
     * - Vertically = **Bottom**
     */
    END(
        { x, width -> x - width },
        { y, height -> y - height }
    ),

    /**
     * Subtracts half the width/height to the input location of the widget. In this case, the widget's
     * center is located on the position.
     *
     * - Horizontally = **Center**
     * - Vertically = **Center**
     */
    CENTER(
        { x, width -> x - width / 2 },
        { y, height -> y - height / 2 }
    );

    /**
     * Invokes the [calcHorizontal] function.
     */
    fun calcHorizontal(x: Double, width: Double) = calcHorizontal.invoke(x, width)

    /**
     * Invokes the [calcVertical] function.
     */
    fun calcVertical(y: Double, height: Double) = calcHorizontal.invoke(y, height)
}