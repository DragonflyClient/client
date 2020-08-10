package net.inceptioncloud.dragonfly.engine.widgets.primitive

import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.internal.annotations.Interpolate
import net.inceptioncloud.dragonfly.engine.internal.annotations.State
import net.inceptioncloud.dragonfly.engine.structure.*
import org.lwjgl.opengl.GL11.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.properties.Delegates

/**
 * ## Filled Circle Primitive Widget
 *
 * A filled version of the [Circle] widget.
 */
class FilledCircle(
    initializerBlock: (FilledCircle.() -> Unit)? = null
) : Widget<FilledCircle>(initializerBlock), IPosition, IColor, IAlign, ISize {

    @Interpolate override var x by Delegates.notNull<Double>()
    @Interpolate override var y by Delegates.notNull<Double>()
    @Interpolate override var size: Double by property(50.0)
    @Interpolate override var color: WidgetColor by property(WidgetColor.DEFAULT)
    @State override var horizontalAlignment: Alignment by property(Alignment.START)
    @State override var verticalAlignment: Alignment by property(Alignment.START)

    init {
        val (alignedX, alignedY) = align(x, y, size, size)
        this.x = alignedX
        this.y = alignedY
    }

    override fun render() {
        color.glBindColor()

        val sections = 50
        val radius = size / 2
        val angle = 2 * Math.PI / sections
        var circleX: Float
        var circleY: Float

        glPushMatrix()
        glEnable(GL_BLEND)
        glDisable(GL_TEXTURE_2D)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glEnable(GL_LINE_SMOOTH)
        glBegin(GL_TRIANGLE_FAN)

        for (i in 0 until sections)
        {
            circleX = (radius * sin(i * angle)).toFloat()
            circleY = (radius * cos(i * angle)).toFloat()
            glVertex2d(x + circleX + radius, y + circleY + radius)
        }

        glEnd()
        glEnable(GL_TEXTURE_2D)
        glDisable(GL_BLEND)
        glDisable(GL_LINE_SMOOTH)
        glPopMatrix()
    }
}