package net.inceptioncloud.minecraftmod.engine.widget

import net.inceptioncloud.minecraftmod.engine.internal.Dynamic
import net.inceptioncloud.minecraftmod.engine.internal.Widget
import net.inceptioncloud.minecraftmod.engine.internal.WidgetColor
import net.inceptioncloud.minecraftmod.engine.structure.IColorable
import net.inceptioncloud.minecraftmod.engine.structure.IPosition
import net.inceptioncloud.minecraftmod.engine.structure.ISize

@Suppress("ConvertSecondaryConstructorToPrimary")
class Circle : Widget<Circle>, IPosition, ISize, IColorable
{
    constructor(
        x: Double = 0.0,
        y: Double = 0.0,
        size: Double = 50.0,
        widgetColor: WidgetColor = WidgetColor.DEFAULT
    ) : super()
    {
        this.x = x
        this.y = y
        this.size = size
        this.widgetColor = widgetColor
    }

    @Dynamic
    override var x: Double = 0.0

    @Dynamic
    override var y: Double = 0.0

    @Dynamic
    override var size: Double = 50.0

    @Dynamic
    override var widgetColor: WidgetColor = WidgetColor(1F, 1F, 1F, 1F)

    override fun render()
    {
        TODO("Not yet implemented")
    }

    override fun clone(): Circle
    {
        return Circle(x, y, size, widgetColor.clone())
    }

    override fun cloneWithPadding(padding: Double): Circle
    {
        return Circle(x + padding, y + padding, size - padding * 2, widgetColor.clone())
    }

    override fun cloneWithMargin(margin: Double): Circle
    {
        return Circle(x - margin, y - margin, size + margin * 2, widgetColor.clone())
    }

    override fun newInstance(): Circle
    {
        return Circle()
    }
}