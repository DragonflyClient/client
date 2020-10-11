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

        val radius = size / 2

        glPushMatrix()
        glEnable(GL_BLEND)
        glDisable(GL_TEXTURE_2D)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        if (smooth) glEnable(GL_POLYGON_SMOOTH)
        glEnable(GL_LINE_SMOOTH)
        glBegin(GL_TRIANGLE_FAN)

        glVertex2d(x + radius, y + radius)

        radians.forEach {
            val offsetX = sin(it) * radius
            val offsetY = cos(it) * radius
            glVertex2d(x + radius + offsetX, y + radius + offsetY)
        }

        glEnd()
        glEnable(GL_TEXTURE_2D)
        glDisable(GL_BLEND)
        glDisable(GL_LINE_SMOOTH)
        if (smooth) glDisable(GL_POLYGON_SMOOTH)
        glPopMatrix()
    }
}