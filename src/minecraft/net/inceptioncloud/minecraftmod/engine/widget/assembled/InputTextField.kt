package net.inceptioncloud.minecraftmod.engine.widget.assembled

import net.inceptioncloud.minecraftmod.Dragonfly
import net.inceptioncloud.minecraftmod.engine.font.WidgetFont
import net.inceptioncloud.minecraftmod.engine.internal.*
import net.inceptioncloud.minecraftmod.engine.internal.annotations.Interpolate
import net.inceptioncloud.minecraftmod.engine.internal.annotations.State
import net.inceptioncloud.minecraftmod.engine.structure.IAlign
import net.inceptioncloud.minecraftmod.engine.structure.IColor
import net.inceptioncloud.minecraftmod.engine.structure.IDimension
import net.inceptioncloud.minecraftmod.engine.structure.IPosition
import net.inceptioncloud.minecraftmod.engine.widget.primitive.Rectangle
import kotlin.properties.Delegates

class InputTextField(
    @property:State var font: WidgetFont = Dragonfly.fontDesign.defaultFont,
    @property:Interpolate var fontSize: Double = 19.0,

    @property:State var label: String? = "Input",
    @property:Interpolate var labelSize: Double = fontSize,

    x: Double = 0.0,
    y: Double = 0.0,
    @property:Interpolate override var width: Double = 100.0,
    @property:Interpolate override var height: Double = 20.0,
    @property:Interpolate override var color: WidgetColor = WidgetColor.DEFAULT,
    @property:State override var horizontalAlignment: Alignment = Alignment.START,
    @property:State override var verticalAlignment: Alignment = Alignment.START
) : AssembledWidget<InputTextField>(), IPosition, IDimension, IAlign, IColor {

    @Interpolate
    override var x: Double by Delegates.notNull()

    @Interpolate
    override var y: Double by Delegates.notNull()

    /**
     * Whether the text field is currently focused. If it is, typed keys will be passed on to the input field
     * and a cursor will be active. When this property changes, the [focusedStateChanged] function will be called.
     */
    private var isFocused = false
        set(value) {
            field = value
            focusedStateChanged(value)
        }

    init {
        val (alignedX, alignedY) = align(x, y, width, height)
        this.x = alignedX
        this.y = alignedY
    }

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "box-round" to RoundedRectangle(),
        "box-sharp" to Rectangle()
    )

    override fun updateStructure() {
        val boxRound = (structure["box-round"] as RoundedRectangle).also {
            it.x = x
            it.y = y
            it.width = width
            it.height = height
            it.arc = 3.0
            it.color = WidgetColor(0xF5F5F5)
        }

        (structure["box-sharp"] as Rectangle).also {
            it.x = x
            it.y = y + boxRound.arc
            it.width = width
            it.height = boxRound.height - boxRound.arc
            it.color = boxRound.color
        }
    }

    /**
     * Called whenever the [isFocused] property changes.
     */
    private fun focusedStateChanged(focused: Boolean) {

    }

    override fun handleMousePress(data: MouseData) {
        isFocused = isHovered

        super.handleMousePress(data)
    }

    override fun clone() = InputTextField(
        font, fontSize, label, labelSize, x, y, width, height, color, horizontalAlignment, verticalAlignment
    )

    override fun newInstance() = InputTextField()
}