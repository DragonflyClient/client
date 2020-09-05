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
) : Widget<FilledCircle>(initializerBlock), IPosition, IColor, IAlign, ISize {

    override var x by property(0.0)
    override var y by property(0.0)
    override var size: Double by property(50.0)
    override var color: WidgetColor by property(WidgetColor.DEFAULT)
    override var horizontalAlignment: Alignment by property(Alignment.START)
    override var verticalAlignment: Alignment by property(Alignment.START)

    var smooth: Boolean by property(false)

    init {
        val (alignedX, alignedY) = align(x, y, size, size)
        this.x = alignedX
        this.y = alignedY
    }

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
            glVertex2d(x + circleX + radius, y + circleY + radius)
        }

        glEnd()
        glEnable(GL_TEXTURE_2D)
        glDisable(GL_BLEND)
        glDisable(GL_LINE_SMOOTH)
        if (smooth) glDisable(GL_POLYGON_SMOOTH)
        glPopMatrix()
    }
}