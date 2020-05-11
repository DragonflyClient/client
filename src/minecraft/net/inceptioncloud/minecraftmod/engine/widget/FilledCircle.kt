package net.inceptioncloud.minecraftmod.engine.widget

import net.inceptioncloud.minecraftmod.engine.internal.Alignment
import net.inceptioncloud.minecraftmod.engine.internal.Dynamic
import net.inceptioncloud.minecraftmod.engine.internal.Widget
import net.inceptioncloud.minecraftmod.engine.internal.WidgetColor
import net.inceptioncloud.minecraftmod.engine.structure.IColor
import net.inceptioncloud.minecraftmod.engine.structure.IPosition
import net.inceptioncloud.minecraftmod.engine.structure.ISize
import org.lwjgl.opengl.GL11.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.properties.Delegates

class FilledCircle(
    x: Double = 0.0,
    y: Double = 00.0,
    size: Double = 50.0,
    widgetColor: WidgetColor = WidgetColor.DEFAULT,
    private val horizontalAlignment: Alignment = Alignment.START,
    private val verticalAlignment: Alignment = Alignment.START
) : Widget<FilledCircle>(), IPosition, ISize, IColor
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
            glVertex2f((x + circleX + radius).toFloat(), (y + circleY + radius).toFloat())
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

    override fun cloneWithPadding(amount: Double): FilledCircle
    {
        val clone = clone()
        val size = clone.size - amount * 2
        val x = clone.horizontalAlignment.reverse(clone.x + amount, size)
        val y = clone.verticalAlignment.reverse(clone.y + amount, size)
        clone.align(x, y, size)
        return clone
    }

    override fun cloneWithMargin(amount: Double): FilledCircle
    {
        val clone = clone()
        val size = clone.size + amount * 2
        val x = clone.horizontalAlignment.reverse(clone.x - amount, size)
        val y = clone.verticalAlignment.reverse(clone.y - amount, size)
        clone.align(x, y, size)
        return clone
    }

    override fun newInstance(): FilledCircle
    {
        return FilledCircle()
    }

    @Dynamic override var x by Delegates.notNull<Double>()
    @Dynamic override var y by Delegates.notNull<Double>()
    @Dynamic override var size by Delegates.notNull<Double>()
    @Dynamic override var widgetColor by Delegates.notNull<WidgetColor>()

    fun align(xIn: Double, yIn: Double, size: Double)
    {
        this.x = horizontalAlignment.calc(xIn, size)
        this.y = verticalAlignment.calc(yIn, size)
        this.size = size
    }

    init
    {
        align(x, y, size)
        this.widgetColor = widgetColor
    }
}