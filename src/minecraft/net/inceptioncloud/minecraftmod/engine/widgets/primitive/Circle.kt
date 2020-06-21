package net.inceptioncloud.minecraftmod.engine.widgets.primitive

import net.inceptioncloud.minecraftmod.engine.internal.*
import net.inceptioncloud.minecraftmod.engine.internal.annotations.Interpolate
import net.inceptioncloud.minecraftmod.engine.internal.annotations.State
import net.inceptioncloud.minecraftmod.engine.structure.*
import org.lwjgl.opengl.GL11.*
import kotlin.math.*
import kotlin.properties.Delegates

/**
 * ## Circle Primitive Widget
 *
 * A simple 360° circle.
 *
 * Note that this circle is not filled but only consists of an outline. The width of
 * this outline can be changed using the [lineWidth] property.
 *
 * @param x X position of the circle. Can be aligned.
 * @param y Y position of the circle. Can be aligned.
 * @param size Width and Height of the circle.
 * @param lineWidth Width of the circle's outline.
 * @param color Color of the circle.
 * @param horizontalAlignment Function to align the circle on the x-axis.
 * @param verticalAlignment Function to align the circle on the y-axis.
 */
@Suppress("LeakingThis")
open class Circle(
    x: Double = 0.0,
    y: Double = 0.0,
    @property:Interpolate override var size: Double = 50.0,
    @property:Interpolate override var color: WidgetColor = WidgetColor.DEFAULT,
    @property:State override var horizontalAlignment: Alignment = Alignment.START,
    @property:State override var verticalAlignment: Alignment = Alignment.START,

    /**
     * The width of the outline of the circle. This value is set during the rendering process
     * using the OpenGL [glLineWidth] function. Notice that high-values can result in errors
     * or ignorance.
     */
    @property:Interpolate var lineWidth: Float = 2F
) : Widget<Circle>(), IPosition, ISize, IColor, IAlign {
    override fun render() {
        color.glBindColor()

        glEnable(GL_LINE_SMOOTH)
        glLineWidth(lineWidth)

        glBegin(GL_LINE_STRIP)
        for (i in 360 downTo 0 step 4)
            glVertex2d(
                x + size / 2 + (cos(i * PI / 180) * size / 2),
                y + size / 2 + (sin(i * PI / 180) * size / 2)
            )

        glEnd()
        glDisable(GL_LINE_SMOOTH)
    }

    override fun clone(): Circle {
        return Circle(
            horizontalAlignment.reverse(x, size),
            verticalAlignment.reverse(y, size),
            size,
            color.clone(),
            horizontalAlignment,
            verticalAlignment,
            lineWidth
        )
    }

    override fun newInstance(): Circle = Circle()

    @Interpolate
    override var x by Delegates.notNull<Double>()

    @Interpolate
    override var y by Delegates.notNull<Double>()

    init {
        val (alignedX, alignedY) = align(x, y, size, size)
        this.x = alignedX
        this.y = alignedY
    }
}