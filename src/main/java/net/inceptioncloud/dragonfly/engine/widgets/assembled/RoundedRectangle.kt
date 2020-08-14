package net.inceptioncloud.dragonfly.engine.widgets.assembled

import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.internal.annotations.Interpolate
import net.inceptioncloud.dragonfly.engine.structure.*
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Arc
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle

/**
 * ## Rounded Rectangle Assembled Widget
 *
 * A simple rectangle with rounded corners.
 *
 * @property arc the size of the corner arc, specifies how rounded the corners are
 */
class RoundedRectangle(
    initializerBlock: (RoundedRectangle.() -> Unit)? = null
) : AssembledWidget<RoundedRectangle>(initializerBlock), IPosition, IDimension, IColor, IAlign {

    @Interpolate override var x: Double by property(0.0)
    @Interpolate override var y: Double by property(0.0)
    @Interpolate override var width: Double by property(50.0)
    @Interpolate override var height: Double by property(50.0)
    @Interpolate override var color: WidgetColor by property(WidgetColor.DEFAULT)
    override var horizontalAlignment: Alignment by property(Alignment.START)
    override var verticalAlignment: Alignment by property(Alignment.START)

    @Interpolate var arc: Double by property(0.0)

    init {
        val (alignedX, alignedY) = align(x, y, width, height)
        this.x = alignedX
        this.y = alignedY

        val smallest = width.coerceAtMost(height) / 2
        this.arc = arc.coerceAtMost(smallest)
    }

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
        structure.forEach { it.value.isVisible = width > arc && height > arc }

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
    }
}