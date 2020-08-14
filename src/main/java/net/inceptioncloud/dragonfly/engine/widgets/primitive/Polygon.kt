package net.inceptioncloud.dragonfly.engine.widgets.primitive

import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.internal.annotations.Interpolate
import net.inceptioncloud.dragonfly.engine.internal.annotations.State
import net.inceptioncloud.dragonfly.engine.structure.*
import org.lwjgl.opengl.GL11.*
import kotlin.math.*
import kotlin.properties.Delegates

/**
 * ## Polygon Primitive Widget
 *
 * Draws a simple polygon with an unlimited number of points. Applies the [lineWidth] and the [color] while
 * rendering.
 *
 * @property points The points of the polygon
 */
open class Polygon(
    initializerBlock: (Polygon.() -> Unit)? = null
) : Widget<Polygon>(initializerBlock), IColor {

    @Interpolate override var color: WidgetColor by property(WidgetColor.DEFAULT)
    @Interpolate var lineWidth: Float by property(2F)

    val points = listOf<Point>()

    override fun render() {
        color.glBindColor()

        glEnable(GL_LINE_SMOOTH)
        glEnable(GL_POLYGON_SMOOTH)
        glLineWidth(lineWidth)

        glBegin(GL_LINE_STRIP)

        for (point in points)
            glVertex2d(point.x.toDouble(), point.y.toDouble())

        glEnd()

        glDisable(GL_POLYGON_SMOOTH)
        glDisable(GL_LINE_SMOOTH)
    }
}