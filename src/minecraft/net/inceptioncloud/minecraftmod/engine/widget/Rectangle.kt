package net.inceptioncloud.minecraftmod.engine.widget

import net.inceptioncloud.minecraftmod.engine.internal.Alignment
import net.inceptioncloud.minecraftmod.engine.internal.Dynamic
import net.inceptioncloud.minecraftmod.engine.internal.Widget
import net.inceptioncloud.minecraftmod.engine.internal.WidgetColor
import net.inceptioncloud.minecraftmod.engine.structure.IColor
import net.inceptioncloud.minecraftmod.engine.structure.IDimension
import net.inceptioncloud.minecraftmod.engine.structure.IOutline
import net.inceptioncloud.minecraftmod.engine.structure.IPosition
import org.lwjgl.opengl.GL11.*
import kotlin.properties.Delegates

class Rectangle(
    x: Double = 0.0,
    y: Double = 00.0,
    width: Double = 50.0,
    height: Double = 50.0,
    widgetColor: WidgetColor = WidgetColor.DEFAULT,
    outlineStroke: Double = 0.0,
    outlineColor: WidgetColor = WidgetColor.DEFAULT,
    private val horizontalAlignment: Alignment = Alignment.START,
    private val verticalAlignment: Alignment = Alignment.START
) : Widget<Rectangle>(), IPosition, IDimension, IColor, IOutline
{
    override fun render()
    {
        if (outlineStroke > 0.0)
        {
            outlineColor.glBindColor()
            glBegin(GL_QUADS)

            val o = outlineStroke

            // red
            glVertex2d(x + width, y - o); glVertex2d(x - o, y - o)
            glVertex2d(x - o, y); glVertex2d(x + width, y)

            // blue
            glVertex2d(x, y); glVertex2d(x - o, y)
            glVertex2d(x - o, y + height + o); glVertex2d(x, y + height + o)

            // green
            glVertex2d(x + width + o, y + height); glVertex2d(x, y + height)
            glVertex2d(x, y + height + o); glVertex2d(x + width + o, y + height + o)

            // yellow
            glVertex2d(x + width + o, y - o); glVertex2d(x + width, y - o)
            glVertex2d(x + width, y + height); glVertex2d(x + width + o, y + height)

            glEnd()
        }

        widgetColor.glBindColor()
        glBegin(GL_QUADS)

        glVertex2d(x + width, y)
        glVertex2d(x, y)
        glVertex2d(x, y + height)
        glVertex2d(x + width, y + height)

        glEnd()
    }

    override fun clone(): Rectangle
    {
        return Rectangle(
            x = horizontalAlignment.reverse(x, width),
            y = verticalAlignment.reverse(y, height),
            width = width,
            height = height,
            widgetColor = widgetColor.clone(),
            outlineStroke = outlineStroke,
            outlineColor = outlineColor.clone(),
            horizontalAlignment = horizontalAlignment,
            verticalAlignment = verticalAlignment
        )
    }

    override fun cloneWithPadding(amount: Double): Rectangle
    {
        val clone = clone()
        val width = clone.width - amount * 2
        val height = clone.height - amount * 2
        val x = clone.horizontalAlignment.reverse(clone.x + amount, width)
        val y = clone.verticalAlignment.reverse(clone.y + amount, height)
        clone.align(x, y, width, height)
        return clone
    }

    override fun cloneWithMargin(amount: Double): Rectangle
    {
        val clone = clone()
        val width = clone.width + amount * 2
        val height = clone.height + amount * 2
        val x = clone.horizontalAlignment.reverse(clone.x - amount, width)
        val y = clone.verticalAlignment.reverse(clone.y - amount, height)
        clone.align(x, y, width, height)
        return clone
    }

    override fun newInstance(): Rectangle
    {
        return Rectangle()
    }

    @Dynamic override var x by Delegates.notNull<Double>()
    @Dynamic override var y by Delegates.notNull<Double>()
    @Dynamic override var width by Delegates.notNull<Double>()
    @Dynamic override var height by Delegates.notNull<Double>()
    @Dynamic override var widgetColor by Delegates.notNull<WidgetColor>()
    @Dynamic override var outlineStroke by Delegates.notNull<Double>()
    @Dynamic override var outlineColor by Delegates.notNull<WidgetColor>()

    fun align(xIn: Double, yIn: Double, widthIn: Double, heightIn: Double)
    {
        this.x = horizontalAlignment.calc(xIn, widthIn)
        this.y = verticalAlignment.calc(yIn, heightIn)
        this.width = widthIn
        this.height = heightIn
    }

    init
    {
        align(x, y, width, height)
        this.widgetColor = widgetColor
        this.outlineStroke = outlineStroke
        this.outlineColor = outlineColor
    }
}