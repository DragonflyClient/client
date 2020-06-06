package net.inceptioncloud.minecraftmod.engine.widget.primitive

import net.inceptioncloud.minecraftmod.engine.internal.Widget
import net.inceptioncloud.minecraftmod.engine.internal.WidgetColor
import net.inceptioncloud.minecraftmod.engine.internal.annotations.Interpolate
import net.inceptioncloud.minecraftmod.engine.structure.IColor
import net.inceptioncloud.minecraftmod.engine.structure.IDimension
import net.inceptioncloud.minecraftmod.engine.structure.IPosition
import net.inceptioncloud.minecraftmod.utils.TimeUtils
import org.apache.logging.log4j.LogManager
import org.lwjgl.opengl.GL11.*

class Line(
    @property:Interpolate override var x: Double = 0.0,
    @property:Interpolate override var y: Double = 0.0,
    @property:Interpolate override var color: WidgetColor = WidgetColor.DEFAULT,

    @property:Interpolate var endX: Double = 10.0,
    @property:Interpolate var endY: Double = 10.0,
    @property:Interpolate var lineWidth: Double = 1.0
) : Widget<Line>(), IPosition, IColor, IDimension {

    override fun render() {
        color.glBindColor()
        glLineWidth(lineWidth.toFloat())

        glBegin(GL_LINES)

        glVertex2d(x, y)
        glVertex2d(endX, endY)

        glEnd()
    }

    override fun clone() = Line(x, y, color.clone(), endX, endY, lineWidth)

    override fun newInstance() = Line()

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

    override var width: Double
        get() = (endX - x).also { supplyDimensionWarning() }
        set(value) {}

    override var height: Double
        get() = (endY - y).also { supplyDimensionWarning() }
        set(value) {}
}