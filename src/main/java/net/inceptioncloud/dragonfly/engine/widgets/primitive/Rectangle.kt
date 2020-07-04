package net.inceptioncloud.dragonfly.engine.widgets.primitive

import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.internal.annotations.Interpolate
import net.inceptioncloud.dragonfly.engine.internal.annotations.State
import net.inceptioncloud.dragonfly.engine.structure.*
import org.lwjgl.opengl.GL11.*
import kotlin.properties.Delegates

/**
 * ## Rectangle Primitive Widget
 *
 * A simple rectangle widget.
 *
 * @param x X position of the rectangle. Can be aligned.
 * @param y Y position of the rectangle. Can be aligned.
 * @param width Width (horizontal size) of the rectangle.
 * @param height Height (vertical size) of the rectangle.
 * @param color Color of the rectangle.
 * @param outlineStroke Width of the outline of the rectangle. Default value is 0 what makes it have no outline.
 * @param outlineColor Color of the outline. Only needed if an outline is set.
 * @param horizontalAlignment Function to align the rectangle on the x-axis.
 * @param verticalAlignment Function to align the rectangle on the y-axis.
 */
class Rectangle(
    x: Double = 0.0,
    y: Double = 0.0,
    @property:Interpolate override var width: Double = 50.0,
    @property:Interpolate override var height: Double = 50.0,
    @property:Interpolate override var color: WidgetColor = WidgetColor.DEFAULT,
    @property:Interpolate override var outlineStroke: Double = 0.0,
    @property:Interpolate override var outlineColor: WidgetColor = WidgetColor.DEFAULT,
    @property:State override var horizontalAlignment: Alignment = Alignment.START,
    @property:State override var verticalAlignment: Alignment = Alignment.START
) : Widget<Rectangle>(), IPosition, IDimension, IColor, IOutline, IAlign {

    override fun render() {
        if (outlineStroke > 0.0) {
            outlineColor.glBindColor()
            glBegin(GL_QUADS)

            val o = outlineStroke

            // red
            glVertex2d(x + width, y - o); glVertex2d(x - o, y - o)
            glVertex2d(x - o, y); glVertex2d(x + width, y)

            // blue
            glVertex2d(x, y); glVertex2d(x - o, y)
            glVertex2d(x - o, y + height + o); glVertex2d(x, y + height + o)

            // green
            glVertex2d(x + width + o, y + height); glVertex2d(x, y + height)
            glVertex2d(x, y + height + o); glVertex2d(x + width + o, y + height + o)

            // yellow
            glVertex2d(x + width + o, y - o); glVertex2d(x + width, y - o)
            glVertex2d(x + width, y + height); glVertex2d(x + width + o, y + height)

            glEnd()
        }

        if (color.alpha > 0) {
            color.glBindColor()
            glBegin(GL_QUADS)

            glVertex2d(x + width, y)
            glVertex2d(x, y)
            glVertex2d(x, y + height)
            glVertex2d(x + width, y + height)

            glEnd()
        }
    }

    override fun clone() = Rectangle(
        x = horizontalAlignment.reverse(x, width),
        y = verticalAlignment.reverse(y, height),
        width = width,
        height = height,
        color = color.clone(),
        outlineStroke = outlineStroke,
        outlineColor = outlineColor.clone(),
        horizontalAlignment = horizontalAlignment,
        verticalAlignment = verticalAlignment
    )

    override fun newInstance(): Rectangle = Rectangle()

    @Interpolate
    override var x: Double by Delegates.notNull()

    @Interpolate
    override var y: Double by Delegates.notNull()

    init {
        val (alignedX, alignedY) = align(x, y, width, height)
        this.x = alignedX
        this.y = alignedY
    }
}