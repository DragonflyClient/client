package net.inceptioncloud.dragonfly.engine.widgets.primitive

import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.internal.annotations.Interpolate
import net.inceptioncloud.dragonfly.engine.structure.*
import org.lwjgl.opengl.GL11.*

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
        if (color.alpha > 0) {
            color.glBindColor()
            glBegin(GL_QUADS)

            glVertex2d(x + width, y)
            glVertex2d(x, y)
            glVertex2d(x, y + height)
            glVertex2d(x + width, y + height)

            glEnd()
        }

        if (outlineStroke > 0.0) {
            outlineColor.glBindColor()
            glLineWidth(2.0F)
            glEnable(GL_LINE_SMOOTH)

            glBegin(GL_LINE_LOOP)

            glVertex2d(x + width, y)
            glVertex2d(x, y)
            glVertex2d(x, y + height)
            glVertex2d(x + width, y + height)

            glEnd()

            glDisable(GL_LINE_SMOOTH)
        }
    }
}