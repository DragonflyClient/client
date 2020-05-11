package net.inceptioncloud.minecraftmod.engine.widget

import net.inceptioncloud.minecraftmod.engine.internal.Alignment
import net.inceptioncloud.minecraftmod.engine.internal.Dynamic
import net.inceptioncloud.minecraftmod.engine.internal.Widget
import net.inceptioncloud.minecraftmod.engine.internal.WidgetColor
import net.inceptioncloud.minecraftmod.engine.structure.IColor
import net.inceptioncloud.minecraftmod.engine.structure.IPosition
import net.inceptioncloud.minecraftmod.engine.structure.ISize
import org.lwjgl.opengl.GL11.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.properties.Delegates

class Circle(
    x: Double = 0.0,
    y: Double = 00.0,
    size: Double = 50.0,
    widgetColor: WidgetColor = WidgetColor.DEFAULT,
    private val horizontalAlignment: Alignment = Alignment.START,
    private val verticalAlignment: Alignment = Alignment.START
) : Widget<Circle>(), IPosition, ISize, IColor
{
    override fun render()
    {
        widgetColor.glBindColor()

        glEnable(GL_LINE_SMOOTH)
        glLineWidth(2F)

        glBegin(GL_LINE_STRIP)
        for (i in 360 downTo 0 step 4)
            glVertex2f(
                (x + size / 2 + (cos(i * PI / 180) * size / 2)).toFloat(),
                (y + size / 2 + (sin(i * PI / 180) * size / 2)).toFloat()
            )

        glEnd()
        glDisable(GL_LINE_SMOOTH)
    }

    override fun clone(): Circle
    {
        return Circle(
            x = horizontalAlignment.reverse(x, size),
            y = verticalAlignment.reverse(y, size),
            size = size,
            widgetColor = widgetColor.clone(),
            horizontalAlignment = horizontalAlignment,
            verticalAlignment = verticalAlignment
        )
    }

    override fun cloneWithPadding(amount: Double): Circle
    {
        val clone = clone()
        val size = clone.size - amount * 2
        val x = clone.horizontalAlignment.reverse(clone.x + amount, size)
        val y = clone.verticalAlignment.reverse(clone.y + amount, size)
        clone.align(x, y, size)
        return clone
    }

    override fun cloneWithMargin(amount: Double): Circle
    {
        val clone = clone()
        val size = clone.size + amount * 2
        val x = clone.horizontalAlignment.reverse(clone.x - amount, size)
        val y = clone.verticalAlignment.reverse(clone.y - amount, size)
        clone.align(x, y, size)
        return clone
    }

    override fun newInstance(): Circle
    {
        return Circle()
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