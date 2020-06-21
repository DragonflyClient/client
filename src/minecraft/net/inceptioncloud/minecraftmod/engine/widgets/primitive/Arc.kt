package net.inceptioncloud.minecraftmod.engine.widgets.primitive

import net.inceptioncloud.minecraftmod.engine.GraphicsEngine
import net.inceptioncloud.minecraftmod.engine.internal.Widget
import net.inceptioncloud.minecraftmod.engine.internal.WidgetColor
import net.inceptioncloud.minecraftmod.engine.internal.annotations.Interpolate
import net.inceptioncloud.minecraftmod.engine.structure.*
import org.lwjgl.opengl.GL11.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * ## Arc Primitive Widget
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
 * @param color Color of the arc.
 */
class Arc(
    @property:Interpolate override var x: Double = 0.0,
    @property:Interpolate override var y: Double = 0.0,
    @property:Interpolate override var width: Double = 50.0,
    @property:Interpolate override var height: Double = 50.0,
    @property:Interpolate override var color: WidgetColor = WidgetColor.DEFAULT,

    @property:Interpolate var start: Int = 0,
    @property:Interpolate var end: Int = 90
) : Widget<Arc>(), IPosition, IDimension, IColor
{
    override fun render()
    {
        val factor = 5.0
        val centerX = x + width / 2
        val centerY = y + height / 2
        GraphicsEngine.pushScale(1 / factor to 1 / factor)

        color.glBindColor()

        glDisable(GL_POLYGON_SMOOTH)
        glBegin(GL_POLYGON)
        glVertex2d(centerX * factor, centerY * factor)

        for (i in end downTo start)
        {
            glVertex2d(
                (centerX + cos(i * Math.PI / 180) * width / 2) * factor,
                (centerY + sin(i * Math.PI / 180) * height / 2) * factor
            )
        }

        glEnd()

        GraphicsEngine.popScale()
    }

    override fun clone(): Arc
    {
        return Arc(
            x = x,
            y = y,
            width = width,
            height = height,
            color = color.clone(),
            start = start,
            end = end
        )
    }

    override fun newInstance(): Arc = Arc()
}