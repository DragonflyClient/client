package net.inceptioncloud.dragonfly.engine.widgets.primitive

import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.structure.*
import org.lwjgl.opengl.GL11.*

/**
 * ## Rectangle Primitive Widget
 *
 * A simple rectangle widget.
 */
class Rectangle(
    initializerBlock: (Rectangle.() -> Unit)? = null
) : Widget<Rectangle>(initializerBlock), IPosition, IDimension, IColor, IOutline {

    override var x: Float by property(0.0F)
    override var y: Float by property(0.0F)
    override var width: Float by property(50.0F)
    override var height: Float by property(50.0F)
    override var color: WidgetColor by property(WidgetColor.DEFAULT)
    override var outlineStroke: Float by property(0.0F)
    override var outlineColor: WidgetColor by property(WidgetColor.DEFAULT)

    override fun render() {
        if (color.alpha > 0) {
            color.glBindColor()
            glBegin(GL_QUADS)

            glVertex2f(x + width, y)
            glVertex2f(x, y)
            glVertex2f(x, y + height)
            glVertex2f(x + width, y + height)

            glEnd()
        }

        if (outlineStroke > 0.0) {
            outlineColor.glBindColor()
            glLineWidth(2.0F)
            glEnable(GL_LINE_SMOOTH)

            glBegin(GL_LINE_LOOP)

            glVertex2f(x + width, y)
            glVertex2f(x, y)
            glVertex2f(x, y + height)
            glVertex2f(x + width, y + height)

            glEnd()

            glDisable(GL_LINE_SMOOTH)
        }
    }
}