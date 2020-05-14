package net.inceptioncloud.minecraftmod.engine.widget.assembled

import net.inceptioncloud.minecraftmod.engine.internal.*
import net.inceptioncloud.minecraftmod.engine.structure.IAlign
import net.inceptioncloud.minecraftmod.engine.structure.IColor
import net.inceptioncloud.minecraftmod.engine.structure.IDimension
import net.inceptioncloud.minecraftmod.engine.structure.IPosition
import net.inceptioncloud.minecraftmod.engine.widget.base.Arc
import net.inceptioncloud.minecraftmod.engine.widget.base.Rectangle
import kotlin.properties.Delegates

class RoundedRectangle(
    x: Double = 0.0,
    y: Double = 0.0,
    width: Double = 50.0,
    height: Double = 50.0,
    widgetColor: WidgetColor = WidgetColor.DEFAULT,
    horizontalAlignment: Alignment = Alignment.START,
    verticalAlignment: Alignment = Alignment.START,

    arc: Double = 5.0
) : AssembledWidget<RoundedRectangle>(), IPosition, IDimension, IColor, IAlign
{
    override fun assemble(): Map<String, Widget<*>>
    {
        return mapOf(
            "left-top-edge" to Arc(),
            "left-bottom-edge" to Arc(),
            "right-bottom-edge" to Arc(),
            "right-top-edge" to Arc(),

            "rect-left" to Rectangle(),
            "rect-right" to Rectangle(),
            "rect-center" to Rectangle()
        )
    }

    override fun updateStructure()
    {
        val smallest = width.coerceAtMost(height) / 2
        arc = arc.coerceAtMost(smallest)

        val spaceVertical = height - arc * 2
        val spaceHorizontal = width - arc * 2

        (structure["rect-left"] as Rectangle).apply {
            x = this@RoundedRectangle.x
            y = this@RoundedRectangle.y + arc
            width = arc
            height = spaceVertical
        }
        (structure["rect-right"] as Rectangle).apply {
            x = this@RoundedRectangle.x + arc + spaceHorizontal
            y = this@RoundedRectangle.y + arc
            width = arc
            height = spaceVertical
        }
        (structure["rect-center"] as Rectangle).apply {
            x = this@RoundedRectangle.x + arc
            y = this@RoundedRectangle.y
            width = spaceHorizontal
            height = this@RoundedRectangle.height
        }

        (structure["left-top-edge"] as Arc).apply {
            x = this@RoundedRectangle.x + arc
            y = this@RoundedRectangle.y + arc
            start = 180
            end = 270
        }
        (structure["left-bottom-edge"] as Arc).apply {
            x = this@RoundedRectangle.x + arc
            y = this@RoundedRectangle.y + arc + spaceVertical
            start = 90
            end = 180
        }
        (structure["right-top-edge"] as Arc).apply {
            x = this@RoundedRectangle.x + arc + spaceHorizontal
            y = this@RoundedRectangle.y + arc
            start = 270
            end = 360
        }
        (structure["right-bottom-edge"] as Arc).apply {
            x = this@RoundedRectangle.x + arc + spaceHorizontal
            y = this@RoundedRectangle.y + arc + spaceVertical
            start = 0
            end = 90
        }

        // changes the color of all base widgets
        structure.values.forEach {
            (it as IColor).widgetColor = this@RoundedRectangle.widgetColor
        }
        // sets the width and height of the arcs
        structure
            .filter { it.key.endsWith("edge") }
            .forEach {
                (it.value as Arc).apply {
                    width = arc
                    height = arc
                }
            }
        initialized = true
    }

    override fun render()
    {
        println(structure.values.sumByDouble { (it as IColor).widgetColor.alphaDouble } / structure.size)
        super.render()
    }

    override fun isStateEqual(clone: RoundedRectangle): Boolean =
        x == clone.x &&
        y == clone.y &&
        width == clone.width &&
        height == clone.height &&
        widgetColor == clone.widgetColor &&
        horizontalAlignment == clone.horizontalAlignment &&
        verticalAlignment == clone.verticalAlignment &&
        arc == clone.arc

    override fun clone(): RoundedRectangle
    {
        return RoundedRectangle(
            x = horizontalAlignment.reverse(x, width),
            y = verticalAlignment.reverse(y, height),
            width = width,
            height = height,
            widgetColor = widgetColor.clone(),
            horizontalAlignment = horizontalAlignment,
            verticalAlignment = verticalAlignment,
            arc = arc
        )
    }

    override fun newInstance(): RoundedRectangle = RoundedRectangle()

    @Dynamic override var x: Double by Delegates.notNull()
    @Dynamic override var y: Double by Delegates.notNull()
    @Dynamic override var width: Double by Delegates.notNull()
    @Dynamic override var height: Double by Delegates.notNull()
    @Dynamic override var widgetColor: WidgetColor by Delegates.notNull()
    @Dynamic override var horizontalAlignment: Alignment by Delegates.notNull()
    @Dynamic override var verticalAlignment: Alignment by Delegates.notNull()

    @Dynamic var arc: Double by Delegates.notNull()

    override fun align(x: Double, y: Double, width: Double, height: Double)
    {
        this.x = horizontalAlignment.calc(x, width)
        this.y = verticalAlignment.calc(y, height)
        this.width = width
        this.height = height
    }

    init
    {
        this.horizontalAlignment = horizontalAlignment
        this.verticalAlignment = verticalAlignment

        align(x, y, width, height)

        this.widgetColor = widgetColor
        this.arc = arc
    }
}

enum class Direction
{
    HORIZONTAL,
    VERTICAL
}