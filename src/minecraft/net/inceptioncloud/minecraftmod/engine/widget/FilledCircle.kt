package net.inceptioncloud.minecraftmod.engine.widget

import net.inceptioncloud.minecraftmod.engine.internal.Alignment
import net.inceptioncloud.minecraftmod.engine.internal.WidgetColor
import org.lwjgl.opengl.GL11.*
import kotlin.math.cos
import kotlin.math.sin

class FilledCircle(
    x: Double = 0.0,
    y: Double = 0.0,
    size: Double = 50.0,
    widgetColor: WidgetColor = WidgetColor.DEFAULT,
    horizontalAlignment: Alignment = Alignment.START,
    verticalAlignment: Alignment = Alignment.START
) : Circle(x, y, size, widgetColor, horizontalAlignment, verticalAlignment)
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

    override fun cloneWithMargin(amount: Double): FilledCircle = super.cloneWithMargin(amount) as FilledCircle

    override fun cloneWithPadding(amount: Double): FilledCircle = super.cloneWithPadding(amount) as FilledCircle
}