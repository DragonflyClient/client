package net.inceptioncloud.minecraftmod.engine.widget

import net.inceptioncloud.minecraftmod.engine.internal.Alignment
import net.inceptioncloud.minecraftmod.engine.internal.Dynamic
import net.inceptioncloud.minecraftmod.engine.internal.Widget
import net.inceptioncloud.minecraftmod.engine.internal.WidgetColor
import net.inceptioncloud.minecraftmod.engine.structure.IColorable
import net.inceptioncloud.minecraftmod.engine.structure.IDimension
import net.inceptioncloud.minecraftmod.engine.structure.IPosition
import org.lwjgl.opengl.GL11.*
import java.awt.Color
import kotlin.properties.Delegates

class Rectangle(
    x: Double = 50.0,
    y: Double = 50.0,
    width: Double = 50.0,
    height: Double = 50.0,
    widgetColor: WidgetColor = WidgetColor.DEFAULT,
    horizontalAlignment: Alignment = Alignment.START,
    verticalAlignment: Alignment = Alignment.START
) : Widget<Rectangle>(), IPosition, IDimension, IColorable
{
    @Dynamic override var x by Delegates.notNull<Double>()
    @Dynamic override var y by Delegates.notNull<Double>()
    @Dynamic override var width by Delegates.notNull<Double>()
    @Dynamic override var height by Delegates.notNull<Double>()
    @Dynamic override var widgetColor by Delegates.notNull<WidgetColor>()

    init
    {
        this.x = horizontalAlignment.calcHorizontal(x, width)
        this.y = verticalAlignment.calcVertical(y, height)
        this.width = width
        this.height = height
        this.widgetColor = widgetColor
    }

    override fun render()
    {
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
        return Rectangle(x, y, width, height, widgetColor.clone())
    }

    override fun cloneWithPadding(padding: Double): Rectangle
    {
        return Rectangle(x + padding, y + padding, width - padding * 2, height - padding * 2, widgetColor.clone())
    }

    override fun cloneWithMargin(margin: Double): Rectangle
    {
        return Rectangle(x - margin, y - margin, width + margin * 2, height + margin * 2, widgetColor.clone())
    }

    override fun newInstance(): Rectangle
    {
        return Rectangle(0.0, 0.0, 0.0, 0.0, WidgetColor(Color.WHITE))
    }
}