package net.inceptioncloud.dragonfly.engine.widgets.assembled

import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.structure.*
import net.inceptioncloud.dragonfly.engine.utils.getCirclePoints
import org.lwjgl.opengl.GL11.*

class RoundRectangle(
    initializerBlock: (RoundRectangle.() -> Unit)? = null
) : Widget<RoundRectangle>(initializerBlock), IPosition, IDimension, IColor {

    override var x: Double by property(0.0)
    override var y: Double by property(0.0)
    override var width: Double by property(50.0)
    override var height: Double by property(50.0)
    override var color: WidgetColor by property(WidgetColor.DEFAULT)

    var arc: Double
        set(value) {
            topLeftArc = value
            topRightArc = value
            bottomLeftArc = value
            bottomRightArc = value
        }
        get() = topLeftArc

    var topLeftArc by property(5.0)
    var topRightArc by property(5.0)
    var bottomRightArc by property(5.0)
    var bottomLeftArc by property(5.0)

    private var outlinePoints: List<Point> = listOf()

    override fun stateChanged() {
        outlinePoints = getCirclePoints(x + width - topRightArc, y + topRightArc, topRightArc, 270..360) +
                getCirclePoints(x + width - bottomRightArc, y + height - bottomRightArc, bottomRightArc, 0..90) +
                getCirclePoints(x + bottomLeftArc, y + height - bottomLeftArc, bottomLeftArc, 90..180) +
                getCirclePoints(x + topLeftArc, y + topLeftArc, topLeftArc, 180..270) +
                getCirclePoints(x + width - topRightArc, y + topRightArc, topRightArc, 270..270)
    }

    override fun render() {
        color.glBindColor()

        glPushMatrix()
        glEnable(GL_BLEND)
        glDisable(GL_TEXTURE_2D)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glEnable(GL_LINE_SMOOTH)
        glBegin(GL_TRIANGLE_FAN)

        outlinePoints.forEach { glVertex2d(it.x, it.y) }

        glEnd()
        glEnable(GL_TEXTURE_2D)
        glDisable(GL_BLEND)
        glDisable(GL_LINE_SMOOTH)
        glPopMatrix()
    }
}