package net.inceptioncloud.minecraftmod.engine.widget.base

import net.inceptioncloud.minecraftmod.engine.internal.Alignment
import net.inceptioncloud.minecraftmod.engine.internal.WidgetColor
import org.lwjgl.opengl.GL11.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * ## Filled Circle Base Widget
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
    size: Double = 50.0,
    widgetColor: WidgetColor = WidgetColor.DEFAULT,
    horizontalAlignment: Alignment = Alignment.START,
    verticalAlignment: Alignment = Alignment.START
) : Circle(x, y, size, 0F, widgetColor, horizontalAlignment, verticalAlignment)
{
    override fun render()
    {
        widgetColor.glBindColor()

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

    override fun clone(): FilledCircle
    {
        return FilledCircle(
            x = horizontalAlignment.reverse(x, size),
            y = verticalAlignment.reverse(y, size),
            size = size,
            widgetColor = widgetColor.clone(),
            horizontalAlignment = horizontalAlignment,
            verticalAlignment = verticalAlignment
        )
    }

    override fun toInfo(): Array<String> = arrayOf(
        "x = $x",
        "y = $y",
        "size = $size",
        "color = $widgetColor",
        "horizontal = ${horizontalAlignment.name}",
        "vertical = ${verticalAlignment.name}"
    )

    override fun cloneWithMargin(amount: Double): FilledCircle = super.cloneWithMargin(amount) as FilledCircle

    override fun cloneWithPadding(amount: Double): FilledCircle = super.cloneWithPadding(amount) as FilledCircle
}