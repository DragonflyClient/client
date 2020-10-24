package net.inceptioncloud.dragonfly.engine.widgets.primitive

import net.inceptioncloud.dragonfly.engine.internal.Widget
import net.inceptioncloud.dragonfly.engine.internal.WidgetColor
import net.inceptioncloud.dragonfly.engine.structure.*
import net.inceptioncloud.dragonfly.utils.TimeUtils
import org.apache.logging.log4j.LogManager
import org.lwjgl.opengl.GL11.*

class Line(
    initializerBlock: (Line.() -> Unit)? = null
) : Widget<Line>(initializerBlock), IPosition, IColor, IDimension {

    override var x: Float by property(0.0F)
    override var y: Float by property(0.0F)
    override var color: WidgetColor by property(WidgetColor.DEFAULT)

    var endX: Float by property(10.0F)
    var endY: Float by property(10.0F)
    var lineWidth: Float by property(1.0F)

    override var width: Float
        get() = (endX - x).also { supplyDimensionWarning() }
        set(_) {}

    override var height: Float
        get() = (endY - y).also { supplyDimensionWarning() }
        set(_) {}

    override fun render() {
        color.glBindColor()
        glLineWidth(lineWidth)

        glBegin(GL_LINES)

        glVertex2f(x, y)
        glVertex2f(endX, endY)

        glEnd()
    }

    /**
     * Checks if the two points are able to create a dimension.
     * If this isn't possible, a warning will be sent to the console.
     */
    private fun supplyDimensionWarning() = TimeUtils.requireDelay("dimension-warning", 10_000) {
        if (x > endX) {
            LogManager.getLogger().warn("[Line] The endX should be greater than the x value, but $x > $endX")
        }
        if (y > endY) {
            LogManager.getLogger().warn("[Line] The endY should be greater than the y value, but $y > $endY")
        }
    }
}