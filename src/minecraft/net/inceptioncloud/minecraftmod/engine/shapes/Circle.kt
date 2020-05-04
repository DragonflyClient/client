package net.inceptioncloud.minecraftmod.engine.shapes

import net.inceptioncloud.minecraftmod.engine.internal.Widget
import net.inceptioncloud.minecraftmod.engine.internal.WidgetColor

class Circle : Widget<Circle>()
{
    /**
     * Contains the core rendering process of the object.
     *
     * This method must be implemented by the object as it is responsible for drawing the object itself.
     * The process is wrapped between the [preRender] and the [postRender] calls.
     */
    override fun render()
    {
        TODO("Not yet implemented")
    }

    /**
     * Clones the graphics object.
     *
     * @return an identical copy of the object that the function was called on
     */
    override fun clone(): Circle
    {
        TODO("Not yet implemented")
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
    override fun cloneWithPadding(padding: Double): Circle
    {
        TODO("Not yet implemented")
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
    override fun cloneWithMargin(margin: Double): Circle
    {
        TODO("Not yet implemented")
    }

    /**
     * Used to create a new instance of the subclass as [Child] is the type of the subclass.
     */
    override fun newInstance(): Circle
    {
        TODO("Not yet implemented")
    }

    /**
     * The x-position of the object on the screen.
     *
     * The x axis is the horizontal axis that increases from the left to the right.
     * This point is part the location of the top-left corner of the object.
     *
     * @see y
     */
    override var x: Double = 0.0

    /**
     * The y-position of the object on the screen.
     *
     * The y axis is the vertical axis that increases from the top to the bottom.
     * This point is part the location of the top-left corner of the object.
     *
     * @see x
     */
    override var y: Double = 0.0

    /**
     * The width of the object.
     *
     * It specifies the size across the horizontal x axis starting from the left to the right.
     *
     * @see height
     */
    override var width: Double = 50.0

    /**
     * The height of the object.
     *
     * It specifies the size across the vertical y axis starting from the top to the bottom.
     *
     * @see width
     */
    override var height: Double = 50.0

    /**
     * The color of the object represented by a [WidgetColor] wrapper.
     */
    override var widgetColor: WidgetColor = WidgetColor(1F, 1F, 1F, 1F)
}