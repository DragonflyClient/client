package net.inceptioncloud.dragonfly.engine.widgets.primitive

import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.structure.*
import org.lwjgl.opengl.GL11.*
import kotlin.math.*

/**
 * ## Circle Primitive Widget
 *
 * A simple 360° circle.
 *
 * Note that this circle is not filled but only consists of an outline. The width of
 * this outline can be changed using the [lineWidth] property.
 *
 * @property size Width and Height of the circle.
 * @property lineWidth Width of the circle's outline.
 * @property lineWidth The width of the outline of the circle. This value is set during the rendering process
 * using the OpenGL [glLineWidth] function. Notice that high-values can result in errors
 * or ignorance.
 */
class Circle(
    initializerBlock: (Circle.() -> Unit)? = null
) : Widget<Circle>(initializerBlock), IPosition, ISize, IColor {

    override var x: Float by property(0.0F)
    override var y: Float by property(0.0F)
    override var size: Float by property(50.0F)
    override var color: WidgetColor by property(WidgetColor.DEFAULT)
    var lineWidth: Float by property(2F)

    override fun render() {
        color.glBindColor()

        glEnable(GL_LINE_SMOOTH)
        glLineWidth(lineWidth)

        glBegin(GL_LINE_STRIP)
        for (i in 360 downTo 0 step 4)
            glVertex2d(
                x + size / 2 + (cos(i * PI / 180) * size / 2),
                y + size / 2 + (sin(i * PI / 180) * size / 2)
            )

        glEnd()
        glDisable(GL_LINE_SMOOTH)
    }
}