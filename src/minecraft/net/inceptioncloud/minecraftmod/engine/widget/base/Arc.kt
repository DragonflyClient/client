package net.inceptioncloud.minecraftmod.engine.widget.base

import net.inceptioncloud.minecraftmod.engine.GraphicsEngine
import net.inceptioncloud.minecraftmod.engine.internal.Dynamic
import net.inceptioncloud.minecraftmod.engine.internal.Widget
import net.inceptioncloud.minecraftmod.engine.internal.WidgetColor
import net.inceptioncloud.minecraftmod.engine.structure.IColor
import net.inceptioncloud.minecraftmod.engine.structure.IDimension
import net.inceptioncloud.minecraftmod.engine.structure.IPosition
import org.lwjgl.opengl.GL11.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.properties.Delegates

/**
 * ## Arc Base Widget
 *
 * This widget allows drawing an arc with any size by specifying the angle in degrees.
 *
 * The x and y always represent the center of the arc and the width and height work like
 * the radius in a circle. The angles are arranged the following way.
 * ```
 *       270°
 *        |
 * 180° --*-- 0°
 *        |
 *       90°
 * ```
 *
 * @param x X coordinate of the arc's center.
 * @param y Y coordinate of the arc's center.
 * @param width Width of the arc. From the center to the outermost point. Horizontal radius for an oval.
 * @param height Height of the arc. From the center to the outermost point. Vertical radius for an oval.
 * @param start Start angle in degrees from 0 - 360. Must be smaller than the [end] angle.
 * @param end End angle in degrees from 0 - 360. Must be greater than the [start] angle.
 * @param widgetColor Color of the arc.
 */
class Arc(
    x: Double = 0.0,
    y: Double = 0.0,
    width: Double = 50.0,
    height: Double = 50.0,
    start: Int = 0,
    end: Int = 90,
    widgetColor: WidgetColor = WidgetColor.DEFAULT
) : Widget<Arc>(), IPosition, IDimension, IColor
{
    override fun render()
    {
        val factor = 5.0
        GraphicsEngine.pushScale(1 / factor)

        widgetColor.glBindColor()
        glEnable(GL_BLEND)
        glEnable(GL_POLYGON_SMOOTH)
        glBegin(GL_POLYGON)
        glVertex2d(x * factor, y * factor)

        for (i in end downTo start)
        {
            glVertex2d(
                (x + cos(i * Math.PI / 180) * width) * factor,
                (y + sin(i * Math.PI / 180) * height) * factor
            )
        }

        glEnd()
        glDisable(GL_POLYGON_SMOOTH)
        glDisable(GL_BLEND)

        glBegin(GL_POLYGON)
        glVertex2d(x * factor, y * factor)

        for (i in end downTo start)
        {
            glVertex2d(
                (x + cos(i * Math.PI / 180) * (width - 1f)) * factor,
                (y + sin(i * Math.PI / 180) * (height - 1f)) * factor
            )
        }

        glEnd()

        GraphicsEngine.popScale()
    }

    override fun isStateChanged(clone: Arc): Boolean =
        x == clone.x &&
        y == clone.y &&
        width == clone.width &&
        height == clone.height &&
        widgetColor == clone.widgetColor &&
        start == clone.start &&
        end == clone.end

    override fun clone(): Arc
    {
        return Arc(
            x = x,
            y = y,
            width = width,
            height = height,
            widgetColor = widgetColor.clone(),
            start = start,
            end = end
        )
    }

    override fun newInstance(): Arc = Arc()

    @Dynamic override var x: Double by Delegates.notNull()
    @Dynamic override var y: Double by Delegates.notNull()
    @Dynamic override var width: Double by Delegates.notNull()
    @Dynamic override var height: Double by Delegates.notNull()
    @Dynamic override var widgetColor: WidgetColor by Delegates.notNull()

    @Dynamic var start: Int by Delegates.notNull()
    @Dynamic var end: Int by Delegates.notNull()

    init
    {
        this.x = x
        this.y = y
        this.width = width
        this.height = height
        this.widgetColor = widgetColor
        this.start = start
        this.end = end
    }
}