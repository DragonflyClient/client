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
 *
 * This widget inherits all functions of the base [Circle] widget but changes the
 * render process. That's why the [lineWidth] property has no use in this widget and
 * will be set to `0F` by default when calling the super constructor.
 *
 * In order to provide full cloning support, the [clone], [cloneWithMargin] and
 * [cloneWithPadding] functions are also overridden.
 *
 * >*The full documentation of the constructor parameters can be found in the [Circle] widget.*
 */
class FilledCircle(
    x: Double = 0.0,
    y: Double = 0.0,
    @property:Interpolate override var size: Double = 50.0,
    @property:Interpolate override var color: WidgetColor = WidgetColor.DEFAULT,
    @property:State override var horizontalAlignment: Alignment = Alignment.START,
    @property:State override var verticalAlignment: Alignment = Alignment.START
) : Widget<FilledCircle>(), IPosition, IColor, IAlign, ISize {

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

    override fun clone(): FilledCircle {
        return FilledCircle(
            x = horizontalAlignment.reverse(x, size),
            y = verticalAlignment.reverse(y, size),
            size = size,
            color = color.clone(),
            horizontalAlignment = horizontalAlignment,
            verticalAlignment = verticalAlignment
        )
    }

    override fun newInstance(): FilledCircle = FilledCircle()

    @Interpolate
    override var x by Delegates.notNull<Double>()

    @Interpolate
    override var y by Delegates.notNull<Double>()

    init {
        val (alignedX, alignedY) = align(x, y, size, size)
        this.x = alignedX
        this.y = alignedY
    }
}