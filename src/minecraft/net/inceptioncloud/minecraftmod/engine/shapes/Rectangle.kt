package net.inceptioncloud.minecraftmod.engine.shapes

import net.inceptioncloud.minecraftmod.engine.internal.Color2D
import net.inceptioncloud.minecraftmod.engine.internal.Dynamic
import net.inceptioncloud.minecraftmod.engine.internal.Shape2D
import org.lwjgl.opengl.GL11.*

class Rectangle : Shape2D<Rectangle>()
{
    /**
     * The x-position of the object on the screen.
     *
     * The x axis is the horizontal axis that increases from the left to the right.
     * This point is part the location of the top-left corner of the object.
     *
     * @see y
     */
    @Dynamic
    override var x: Double = 0.0

    /**
     * The y-position of the object on the screen.
     *
     * The y axis is the vertical axis that increases from the top to the bottom.
     * This point is part the location of the top-left corner of the object.
     *
     * @see x
     */
    @Dynamic
    override var y: Double = 0.0

    /**
     * The width of the object.
     *
     * It specifies the size across the horizontal x axis starting from the left to the right.
     *
     * @see height
     */
    @Dynamic
    override var width: Double = 50.0

    /**
     * The height of the object.
     *
     * It specifies the size across the vertical y axis starting from the top to the bottom.
     *
     * @see width
     */
    @Dynamic
    override var height: Double = 50.0

    /**
     * The color of the object represented by a [Color2D] wrapper.
     */
    override var color: Color2D = Color2D(1.0, 1.0, 1.0, 1.0)

    /**
     * Contains the core rendering process of the object.
     *
     * This method must be implemented by the object as it is responsible for drawing the object itself.
     * The process is wrapped between the [preRender] and the [postRender] calls.
     */
    override fun render()
    {
        color.glBindColor()

        glBegin(GL_QUADS)

        glVertex2d(x, y)
        glVertex2d(x + width, y)
        glVertex2d(x + width, y + height)
        glVertex2d(x, y + height)

        glEnd()
    }

    /**
     * Clones the graphics object.
     *
     * @return an identical copy of the object that the function was called on
     */
    override fun clone(): Rectangle
    {
        return Rectangle().static(x, y, width, height, color)
    }

    /**
     * Clones the graphics object and adds a padding.
     *
     * By adding a padding, the cloned object will get smaller. It stays in the center of the original
     * object, and the distance between the outline of the cloned object and the outline of the original
     * object is equal to the padding size.
     *
     * @see cloneWithMargin
     * @return a congruent copy of the object with the given padding to the original object
     */
    override fun cloneWithPadding(padding: Double): Rectangle
    {
        return Rectangle().static(x + padding, y + padding, width - padding * 2, height - padding * 2, color)
    }

    /**
     * Clones the graphics object and adds a margin.
     *
     * By adding a padding, the cloned object will get greater. It stays in the center of the original
     * object, and the distance between the outline of the original object and the outline of the cloned
     * object is equal to the padding size.
     *
     * @see cloneWithPadding
     * @return a congruent copy of the object with the given margin to the original object
     */
    override fun cloneWithMargin(margin: Double): Rectangle
    {
        return Rectangle().static(x - margin, y - margin, width + margin * 2, height + margin * 2, color)
    }
}