package net.inceptioncloud.dragonfly.engine.widgets.primitive

import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.internal.annotations.Interpolate
import net.inceptioncloud.dragonfly.engine.structure.*
import org.lwjgl.opengl.GL11.*
import kotlin.properties.Delegates

/**
 * ## Rectangle Primitive Widget
 *
 * A simple rectangle widget.
 */
class Rectangle(
    initializerBlock: (Rectangle.() -> Unit)? = null
) : Widget<Rectangle>(initializerBlock), IPosition, IDimension, IColor, IOutline, IAlign {

    @Interpolate override var x: Double by property(0.0)
    @Interpolate override var y: Double by property(0.0)
    @Interpolate override var width: Double by property(50.0)
    @Interpolate override var height: Double by property(50.0)
    @Interpolate override var color: WidgetColor by property(WidgetColor.DEFAULT)
    @Interpolate override var outlineStroke: Double by property(0.0)
    @Interpolate override var outlineColor: WidgetColor by property(WidgetColor.DEFAULT)
    override var horizontalAlignment: Alignment by property(Alignment.START)
    override var verticalAlignment: Alignment by property(Alignment.START)

    init {
        val (alignedX, alignedY) = align(x, y, width, height)
        this.x = alignedX
        this.y = alignedY
    }

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
}