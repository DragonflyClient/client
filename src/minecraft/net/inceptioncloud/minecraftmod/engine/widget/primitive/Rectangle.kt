package net.inceptioncloud.minecraftmod.engine.widget.primitive

import net.inceptioncloud.minecraftmod.engine.internal.Alignment
import net.inceptioncloud.minecraftmod.engine.internal.Dynamic
import net.inceptioncloud.minecraftmod.engine.internal.Widget
import net.inceptioncloud.minecraftmod.engine.internal.WidgetColor
import net.inceptioncloud.minecraftmod.engine.structure.*
import org.lwjgl.opengl.GL11.*
import kotlin.properties.Delegates

/**
 * ## Rectangle Primitive Widget
 *
 * A simple rectangle widget.
 *
 * @param x X position of the rectangle. Can be aligned.
 * @param y Y position of the rectangle. Can be aligned.
 * @param width Width (horizontal size) of the rectangle.
 * @param height Height (vertical size) of the rectangle.
 * @param widgetColor Color of the rectangle.
 * @param outlineStroke Width of the outline of the rectangle. Default value is 0 what makes it have no outline.
 * @param outlineColor Color of the outline. Only needed if an outline is set.
 * @param horizontalAlignment Function to align the rectangle on the x-axis.
 * @param verticalAlignment Function to align the rectangle on the y-axis.
 */
class Rectangle(
    x: Double = 0.0,
    y: Double = 0.0,
    width: Double = 50.0,
    height: Double = 50.0,
    widgetColor: WidgetColor = WidgetColor.DEFAULT,
    outlineStroke: Double = 0.0,
    outlineColor: WidgetColor = WidgetColor.DEFAULT,
    horizontalAlignment: Alignment = Alignment.START,
    verticalAlignment: Alignment = Alignment.START
) : Widget<Rectangle>(), IPosition, IDimension, IColor, IOutline, IAlign {
    override fun render() {
        if (outlineStroke > 0.0) {
            outlineColor.glBindColor()
            glBegin(GL_QUADS)

            val o = outlineStroke

            // red
            glVertex2d(x + width, y - o); glVertex2d(x - o, y - o)
            glVertex2d(x - o, y); glVertex2d(x + width, y)

            // blue
            glVertex2d(x, y); glVertex2d(x - o, y)
            glVertex2d(x - o, y + height + o); glVertex2d(x, y + height + o)

            // green
            glVertex2d(x + width + o, y + height); glVertex2d(x, y + height)
            glVertex2d(x, y + height + o); glVertex2d(x + width + o, y + height + o)

            // yellow
            glVertex2d(x + width + o, y - o); glVertex2d(x + width, y - o)
            glVertex2d(x + width, y + height); glVertex2d(x + width + o, y + height)

            glEnd()
        }

        if (widgetColor.alpha > 0) {
            widgetColor.glBindColor()
            glBegin(GL_QUADS)

            glVertex2d(x + width, y)
            glVertex2d(x, y)
            glVertex2d(x, y + height)
            glVertex2d(x + width, y + height)

            glEnd()
        }
    }

    override fun isStateEqual(clone: Rectangle): Boolean =
        x == clone.x &&
                y == clone.y &&
                width == clone.width &&
                height == clone.height &&
                widgetColor == clone.widgetColor &&
                outlineStroke == clone.outlineStroke &&
                outlineColor == clone.outlineColor

    override fun clone(): Rectangle {
        return Rectangle(
            x = horizontalAlignment.reverse(x, width),
            y = verticalAlignment.reverse(y, height),
            width = width,
            height = height,
            widgetColor = widgetColor.clone(),
            outlineStroke = outlineStroke,
            outlineColor = outlineColor.clone(),
            horizontalAlignment = horizontalAlignment,
            verticalAlignment = verticalAlignment
        )
    }

    override fun toInfo(): Array<String> = arrayOf(
        "x = $x",
        "y = $y",
        "width = $width",
        "height = $height",
        "color = $widgetColor",
        "outlineStroke = $outlineStroke",
        "outlineColor = $outlineColor",
        "horizontal = ${horizontalAlignment.name}",
        "vertical = ${verticalAlignment.name}"
    )

    override fun newInstance(): Rectangle = Rectangle()

    @Dynamic
    override var x: Double by Delegates.notNull()

    @Dynamic
    override var y: Double by Delegates.notNull()

    @Dynamic
    override var width: Double by Delegates.notNull()

    @Dynamic
    override var height: Double by Delegates.notNull()

    @Dynamic
    override var widgetColor: WidgetColor by Delegates.notNull()

    @Dynamic
    override var outlineStroke: Double by Delegates.notNull()
    @Dynamic
    override var outlineColor: WidgetColor by Delegates.notNull()

    override var horizontalAlignment: Alignment by Delegates.notNull()
    override var verticalAlignment: Alignment by Delegates.notNull()

    override fun align(x: Double, y: Double, width: Double, height: Double) {
        this.x = horizontalAlignment.calc(x, width)
        this.y = verticalAlignment.calc(y, height)
        this.width = width
        this.height = height
    }

    init {
        this.horizontalAlignment = horizontalAlignment
        this.verticalAlignment = verticalAlignment

        align(x, y, width, height)

        this.widgetColor = widgetColor
        this.outlineStroke = outlineStroke
        this.outlineColor = outlineColor
    }
}