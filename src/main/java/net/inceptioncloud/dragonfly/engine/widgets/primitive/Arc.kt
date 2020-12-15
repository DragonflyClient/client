package net.inceptioncloud.dragonfly.engine.widgets.primitive

import net.inceptioncloud.dragonfly.engine.GraphicsEngine
import net.inceptioncloud.dragonfly.engine.internal.Widget
import net.inceptioncloud.dragonfly.engine.internal.WidgetColor
import net.inceptioncloud.dragonfly.engine.structure.*
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
 * @property x X coordinate of the arc's center.
 * @property y Y coordinate of the arc's center.
 * @property width Width of the arc. From the center to the outermost point. Horizontal radius for an oval.
 * @property height Height of the arc. From the center to the outermost point. Vertical radius for an oval.
 * @property start Start angle in degrees from 0 - 360. Must be smaller than the [end] angle.
 * @property end End angle in degrees from 0 - 360. Must be greater than the [start] angle.
 */
class Arc(
    initializerBlock: (Arc.() -> Unit)? = null
) : Widget<Arc>(initializerBlock), IPosition, IDimension, IColor {

    override var x: Double by property(0.0)
    override var y: Double by property(0.0)
    override var width: Double by property(50.0)
    override var height: Double by property(50.0)
    override var color: WidgetColor by property(WidgetColor.DEFAULT)

    var start: Int by property(0)
    var end: Int by property(90)

    override fun render() {
        val factor = 5.0
        val centerX = x + width / 2
        val centerY = y + height / 2
        GraphicsEngine.pushScale(1 / factor)

        color.glBindColor()

        glDisable(GL_POLYGON_SMOOTH)
        glBegin(GL_POLYGON)
        glVertex2d(centerX * factor, centerY * factor)

        for (i in end downTo start) {
            glVertex2d(
                (centerX + cos(i * Math.PI / 180) * width / 2) * factor,
                (centerY + sin(i * Math.PI / 180) * height / 2) * factor
            )
        }

        glEnd()

        GraphicsEngine.popScale()
    }
}