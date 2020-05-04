package net.inceptioncloud.minecraftmod.engine.shapes

import net.inceptioncloud.minecraftmod.engine.internal.Dynamic
import net.inceptioncloud.minecraftmod.engine.internal.Widget
import net.inceptioncloud.minecraftmod.engine.internal.WidgetColor
import org.lwjgl.opengl.GL11.*

class Rectangle : Widget<Rectangle>()
{
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

    override fun newInstance(): Rectangle
    {
        return Rectangle()
    }

    override fun clone(): Rectangle
    {
        return Rectangle().static(x, y, width, height, widgetColor.clone())
    }

    override fun cloneWithPadding(padding: Double): Rectangle
    {
        return Rectangle().static(x + padding, y + padding, width - padding * 2, height - padding * 2, widgetColor.clone())
    }

    override fun cloneWithMargin(margin: Double): Rectangle
    {
        return Rectangle().static(x - margin, y - margin, width + margin * 2, height + margin * 2, widgetColor.clone())
    }

    @Dynamic
    override var x: Double = 0.0

    @Dynamic
    override var y: Double = 0.0

    @Dynamic
    override var width: Double = 50.0

    @Dynamic
    override var height: Double = 50.0

    @Dynamic
    override var widgetColor: WidgetColor = WidgetColor(1.0, 1.0, 1.0, 1.0)
}