package net.inceptioncloud.minecraftmod.engine.widget

import net.inceptioncloud.minecraftmod.engine.internal.Dynamic
import net.inceptioncloud.minecraftmod.engine.internal.Widget
import net.inceptioncloud.minecraftmod.engine.internal.WidgetColor
import net.inceptioncloud.minecraftmod.engine.structure.IColorable
import net.inceptioncloud.minecraftmod.engine.structure.IDimension
import net.inceptioncloud.minecraftmod.engine.structure.IPosition
import org.lwjgl.opengl.GL11.*
import java.awt.Color

@Suppress("ConvertSecondaryConstructorToPrimary")
class Rectangle : Widget<Rectangle>, IPosition, IDimension, IColorable
{
    constructor(x: Double, y: Double, width: Double, height: Double, widgetColor: WidgetColor) : super()
    {
        this.x = x
        this.y = y
        this.width = width
        this.height = height
        this.widgetColor = widgetColor
    }

    @Dynamic
    override var x: Double = 50.0

    @Dynamic
    override var y: Double = 50.0

    @Dynamic
    override var width: Double = 50.0

    @Dynamic
    override var height: Double = 50.0

    @Dynamic
    override var widgetColor: WidgetColor = WidgetColor(Color.WHITE)

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