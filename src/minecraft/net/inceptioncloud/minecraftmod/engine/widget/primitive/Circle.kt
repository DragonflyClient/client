package net.inceptioncloud.minecraftmod.engine.widget.primitive

import net.inceptioncloud.minecraftmod.engine.internal.Alignment
import net.inceptioncloud.minecraftmod.engine.internal.Dynamic
import net.inceptioncloud.minecraftmod.engine.internal.Widget
import net.inceptioncloud.minecraftmod.engine.internal.WidgetColor
import net.inceptioncloud.minecraftmod.engine.structure.IAlign
import net.inceptioncloud.minecraftmod.engine.structure.IColor
import net.inceptioncloud.minecraftmod.engine.structure.IPosition
import net.inceptioncloud.minecraftmod.engine.structure.ISize
import org.lwjgl.opengl.GL11.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
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
 * @param widgetColor Color of the circle.
 * @param horizontalAlignment Function to align the circle on the x-axis.
 * @param verticalAlignment Function to align the circle on the y-axis.
 */
@Suppress("LeakingThis")
open class Circle(
    x: Double = 0.0,
    y: Double = 0.0,
    size: Double = 50.0,
    lineWidth: Float = 2F,
    widgetColor: WidgetColor = WidgetColor.DEFAULT,
    horizontalAlignment: Alignment = Alignment.START,
    verticalAlignment: Alignment = Alignment.START
) : Widget<Circle>(), IPosition, ISize, IColor, IAlign
{
    override fun render()
    {
        widgetColor.glBindColor()

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

    override fun isStateEqual(clone: Circle): Boolean =
        x == clone.x &&
        y == clone.y &&
        size == clone.size &&
        widgetColor == clone.widgetColor &&
        horizontalAlignment == clone.horizontalAlignment &&
        verticalAlignment == clone.verticalAlignment &&
        lineWidth == clone.lineWidth

    override fun clone(): Circle
    {
        return Circle(
            x = horizontalAlignment.reverse(x, size),
            y = verticalAlignment.reverse(y, size),
            size = size,
            widgetColor = widgetColor.clone(),
            horizontalAlignment = horizontalAlignment,
            verticalAlignment = verticalAlignment
        )
    }

    override fun toInfo(): Array<String> = arrayOf(
        "x = $x",
        "y = $y",
        "size = $size",
        "color = $widgetColor",
        "horizontal = ${horizontalAlignment.name}",
        "vertical = ${verticalAlignment.name}"
    )

    override fun newInstance(): Circle = Circle()

    @Dynamic override var x by Delegates.notNull<Double>()
    @Dynamic override var y by Delegates.notNull<Double>()
    @Dynamic override var size by Delegates.notNull<Double>()
    @Dynamic override var widgetColor by Delegates.notNull<WidgetColor>()
    @Dynamic override var horizontalAlignment: Alignment by Delegates.notNull()
    @Dynamic override var verticalAlignment: Alignment by Delegates.notNull()

    /**
     * The width of the outline of the circle. This value is set during the rendering process
     * using the OpenGL [glLineWidth] function. Notice that high-values can result in errors
     * or ignorance.
     */
    @Dynamic var lineWidth: Float by Delegates.notNull()

    override fun align(x: Double, y: Double, width: Double, height: Double)
    {
        assert(width == height)

        this.x = horizontalAlignment.calc(x, width)
        this.y = verticalAlignment.calc(y, height)
        this.size = width
    }

    init
    {
        this.horizontalAlignment = horizontalAlignment
        this.verticalAlignment = verticalAlignment

        align(x, y, size, size)

        this.widgetColor = widgetColor
        this.lineWidth = lineWidth
    }
}