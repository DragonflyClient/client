package net.inceptioncloud.dragonfly.engine.widgets.primitive

import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.structure.*
import org.lwjgl.opengl.GL11.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * ## Filled Circle Primitive Widget
 *
 * A filled version of the [Circle] widget.
 */
class FilledCircle(
    initializerBlock: (FilledCircle.() -> Unit)? = null
) : Widget<FilledCircle>(initializerBlock), IPosition, IColor, ISize {

    override var x: Float by property(0.0F)
    override var y: Float by property(0.0F)
    override var size: Float by property(50.0F)
    override var color: WidgetColor by property(WidgetColor.DEFAULT)

    var smooth: Boolean by property(false)

    override fun render() {
        color.glBindColor()

        val sections = 150
        val radius = size / 2
        val angle = 2 * Math.PI / sections
        var circleX: Float
        var circleY: Float

        glPushMatrix()
        glEnable(GL_BLEND)
        glDisable(GL_TEXTURE_2D)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        if (smooth) glEnable(GL_POLYGON_SMOOTH)
        glEnable(GL_LINE_SMOOTH)
        glBegin(GL_TRIANGLE_FAN)

        for (i in 0 until sections) {
            circleX = (radius * sin(i * angle)).toFloat()
            circleY = (radius * cos(i * angle)).toFloat()
            glVertex2f(x + circleX + radius, y + circleY + radius)
        }

        glEnd()
        glEnable(GL_TEXTURE_2D)
        glDisable(GL_BLEND)
        glDisable(GL_LINE_SMOOTH)
        if (smooth) glDisable(GL_POLYGON_SMOOTH)
        glPopMatrix()
    }
}