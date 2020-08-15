package net.inceptioncloud.dragonfly.engine.widgets.primitive

import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.internal.annotations.Interpolate
import net.inceptioncloud.dragonfly.engine.structure.*
import org.lwjgl.opengl.GL11.*
import kotlin.math.*
import kotlin.properties.Delegates

/**
 * ## Polygon Primitive Widget
 *
 * Draws a simple polygon with an unlimited number of points. Applies the [color] while
 * rendering.
 *
 * @property points The points of the polygon
 * @property smooth whether the flag [GL_POLYGON_SMOOTH] should be enabled while rendering
 * which makes the polygon look smoother but can lead to issues with the alpha channel
 */
open class Polygon(
    initializerBlock: (Polygon.() -> Unit)? = null
) : Widget<Polygon>(initializerBlock), IColor {

    @Interpolate override var color: WidgetColor by property(WidgetColor.DEFAULT)

    var smooth: Boolean by property(false)
    val points = mutableListOf<Point>()

    override fun render() {
        color.glBindColor()

        glPushMatrix()

        glEnable(GL_BLEND)
        glDisable(GL_TEXTURE_2D)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glEnable(GL_LINE_SMOOTH)
        if (smooth) glEnable(GL_POLYGON_SMOOTH)

        glBegin(GL_TRIANGLE_FAN)

        for (point in points)
            glVertex2d(point.x.toDouble(), point.y.toDouble())

        glEnd()

        glEnable(GL_TEXTURE_2D)
        glDisable(GL_BLEND)
        glDisable(GL_LINE_SMOOTH)
        if (smooth) glDisable(GL_POLYGON_SMOOTH)

        glPopMatrix()
    }
}