package net.inceptioncloud.minecraftmod.engine.widget.assembled

import net.inceptioncloud.minecraftmod.engine.internal.Alignment
import net.inceptioncloud.minecraftmod.engine.internal.AssembledWidget
import net.inceptioncloud.minecraftmod.engine.internal.Widget
import net.inceptioncloud.minecraftmod.engine.internal.WidgetColor
import net.inceptioncloud.minecraftmod.engine.internal.annotations.Interpolate
import net.inceptioncloud.minecraftmod.engine.internal.annotations.State
import net.inceptioncloud.minecraftmod.engine.structure.IAlign
import net.inceptioncloud.minecraftmod.engine.structure.IColor
import net.inceptioncloud.minecraftmod.engine.structure.IDimension
import net.inceptioncloud.minecraftmod.engine.structure.IPosition
import kotlin.properties.Delegates

class InputTextField(
    @property:State var label: String = "Input",

    x: Double = 0.0,
    y: Double = 0.0,
    @property:Interpolate override var width: Double = 100.0,
    @property:Interpolate override var height: Double = 20.0,
    @property:State override var horizontalAlignment: Alignment = Alignment.START,
    @property:State override var verticalAlignment: Alignment = Alignment.START,
    @property:Interpolate override var widgetColor: WidgetColor = WidgetColor.DEFAULT
) : AssembledWidget<InputTextField>(), IPosition, IDimension, IAlign, IColor {

    @Interpolate
    override var x: Double by Delegates.notNull()
    @Interpolate
    override var y: Double by Delegates.notNull()

    init {
        val (alignedX, alignedY) = align(x, y, width, height)
        this.x = alignedX
        this.y = alignedY
    }

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "label" to TextField()
    )

    override fun updateStructure() {
        TODO("Not yet implemented")
    }

    override fun clone(): InputTextField {
        TODO("Not yet implemented")
    }

    override fun newInstance() = InputTextField()
}