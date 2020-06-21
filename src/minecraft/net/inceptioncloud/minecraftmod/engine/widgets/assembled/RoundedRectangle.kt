package net.inceptioncloud.minecraftmod.engine.widgets.assembled

import net.inceptioncloud.minecraftmod.engine.internal.*
import net.inceptioncloud.minecraftmod.engine.internal.annotations.Interpolate
import net.inceptioncloud.minecraftmod.engine.internal.annotations.State
import net.inceptioncloud.minecraftmod.engine.structure.*
import net.inceptioncloud.minecraftmod.engine.widgets.primitive.Arc
import net.inceptioncloud.minecraftmod.engine.widgets.primitive.Rectangle
import kotlin.properties.Delegates

/**
 * ## Rounded Rectangle Assembled Widget
 *
 * A simple rectangle with rounded corners.
 *
 * @param x X position of the rectangle. Can be aligned.
 * @param y Y position of the rectangle. Can be aligned.
 * @param width Width (horizontal size) of the rectangle.
 * @param height Height (vertical size) of the rectangle.
 * @param color Color of the rectangle.
 * @param horizontalAlignment Function to align the rectangle on the x-axis.
 * @param verticalAlignment Function to align the rectangle on the y-axis.
 * @param arc the size of the corner arc, specifies how rounded the corners are
 */
class RoundedRectangle(
    x: Double = 0.0,
    y: Double = 0.0,
    @property:Interpolate override var width: Double = 50.0,
    @property:Interpolate override var height: Double = 50.0,
    @property:Interpolate override var color: WidgetColor = WidgetColor.DEFAULT,
    @property:State override var horizontalAlignment: Alignment = Alignment.START,
    @property:State override var verticalAlignment: Alignment = Alignment.START,

    arc: Double = 5.0
) : AssembledWidget<RoundedRectangle>(), IPosition, IDimension, IColor, IAlign {

    override fun assemble(): Map<String, Widget<*>> {
        return mapOf(
            "left-top-edge" to Arc(),
            "left-bottom-edge" to Arc(),
            "right-bottom-edge" to Arc(),
            "right-top-edge" to Arc(),

            "rect-left" to Rectangle(),
            "rect-right" to Rectangle(),
            "rect-center" to Rectangle()
        )
    }

    override fun updateStructure() {
        val spaceVertical = height - arc * 2
        val spaceHorizontal = width - arc * 2

        (structure["rect-left"] as Rectangle).apply {
            x = this@RoundedRectangle.x
            y = this@RoundedRectangle.y + arc
            width = arc
            height = spaceVertical
        }
        (structure["rect-right"] as Rectangle).apply {
            x = this@RoundedRectangle.x + arc + spaceHorizontal
            y = this@RoundedRectangle.y + arc
            width = arc
            height = spaceVertical
        }
        (structure["rect-center"] as Rectangle).apply {
            x = this@RoundedRectangle.x + arc
            y = this@RoundedRectangle.y
            width = spaceHorizontal
            height = this@RoundedRectangle.height
        }

        (structure["left-top-edge"] as Arc).apply {
            x = this@RoundedRectangle.x
            y = this@RoundedRectangle.y
            start = 180
            end = 270
        }
        (structure["left-bottom-edge"] as Arc).apply {
            x = this@RoundedRectangle.x
            y = this@RoundedRectangle.y + spaceVertical
            start = 90
            end = 180
        }
        (structure["right-top-edge"] as Arc).apply {
            x = this@RoundedRectangle.x + spaceHorizontal
            y = this@RoundedRectangle.y
            start = 270
            end = 360
        }
        (structure["right-bottom-edge"] as Arc).apply {
            x = this@RoundedRectangle.x + spaceHorizontal
            y = this@RoundedRectangle.y + spaceVertical
            start = 0
            end = 90
        }

        // changes the color of all base widgets
        structure.values.forEach {
            (it as IColor).color = this@RoundedRectangle.color
        }
        // sets the width and height of the arcs
        structure
            .filter { it.key.endsWith("edge") }
            .forEach {
                (it.value as Arc).apply {
                    width = arc * 2
                    height = arc * 2
                }
            }
        initialized = true
    }

    override fun clone(): RoundedRectangle {
        return RoundedRectangle(
            x = horizontalAlignment.reverse(x, width),
            y = verticalAlignment.reverse(y, height),
            width = width,
            height = height,
            color = color.clone(),
            horizontalAlignment = horizontalAlignment,
            verticalAlignment = verticalAlignment,
            arc = arc
        )
    }

    override fun newInstance(): RoundedRectangle = RoundedRectangle()

    @Interpolate
    override var x: Double by Delegates.notNull()

    @Interpolate
    override var y: Double by Delegates.notNull()

    @Interpolate
    var arc: Double by Delegates.notNull()

    init {
        val (alignedX, alignedY) = align(x, y, width, height)
        this.x = alignedX
        this.y = alignedY

        val smallest = width.coerceAtMost(height) / 2
        this.arc = arc.coerceAtMost(smallest)
    }
}