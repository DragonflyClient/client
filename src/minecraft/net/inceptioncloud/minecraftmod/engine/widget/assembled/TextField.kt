package net.inceptioncloud.minecraftmod.engine.widget.assembled

import net.inceptioncloud.minecraftmod.Dragonfly
import net.inceptioncloud.minecraftmod.engine.font.IFontRenderer
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
import net.inceptioncloud.minecraftmod.engine.widget.primitive.Rectangle
import net.inceptioncloud.minecraftmod.engine.widget.primitive.TextRenderer
import kotlin.properties.Delegates

class TextField(
    @property:State var staticText: String = "Text Field",
    @property:State var dynamicText: (() -> String)? = null,
    @property:State var textAlignHorizontal: Alignment = Alignment.START,
    @property:State var textAlignVertical: Alignment = Alignment.START,
    @property:State var fontRenderer: IFontRenderer = Dragonfly.fontDesign.regular,

    @property:Interpolate var backgroundColor: WidgetColor = WidgetColor.DEFAULT,
    @property:Interpolate var outlineStroke: Double = 0.0,
    @property:Interpolate var outlineColor: WidgetColor = WidgetColor.DEFAULT,

    x: Double = 0.0,
    y: Double = 0.0,
    @property:Interpolate override var width: Double = 50.0,
    @property:Interpolate override var height: Double = 50.0,
    @property:Interpolate override var widgetColor: WidgetColor = WidgetColor.DEFAULT,
    @property:State override var horizontalAlignment: Alignment = Alignment.START,
    @property:State override var verticalAlignment: Alignment = Alignment.START
) : AssembledWidget<TextField>(), IPosition, IDimension, IColor, IAlign {

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
        "background" to Rectangle(),
        "text" to TextRenderer()
    )

    override fun updateStructure() {
        val text = (structure["text"] as TextRenderer).also {
            it.fontRenderer = fontRenderer
            it.x = alignText(textAlignHorizontal, x, width, fontRenderer.getStringWidth(it.text).toDouble())
            it.y = alignText(textAlignVertical, y, height, fontRenderer.height.toDouble())
            it.widgetColor = widgetColor
            it.text = getText()
        }

        (structure["background"] as Rectangle).also {
            it.x = x
            it.y = y
            it.width = width
            it.height = height
            it.widgetColor = backgroundColor
            it.outlineColor = outlineColor
            it.outlineStroke = outlineStroke
        }
    }

    /**
     * Returns the current text of the field. This is the [dynamicText] or the [staticText], if no
     * [dynamicText] is set.
     */
    private fun getText() = dynamicText?.invoke() ?: staticText

    /**
     * A special alignment function for the text inside of the text field.
     *
     * @param alignment the alignment of the text
     * @param coordinate the original coordinate (x, y)
     * @param size the size for the given dimension (width, height)
     * @param textSize the text size for the given dimension (text width, text height)
     */
    private fun alignText(alignment: Alignment, coordinate: Double, size: Double, textSize: Double): Double =
        when (alignment) {
            Alignment.START -> coordinate
            Alignment.CENTER -> coordinate + (size / 2) - (textSize / 2)
            Alignment.END -> coordinate + size - textSize
        }

    override fun clone() = TextField(
        staticText,
        dynamicText,
        textAlignHorizontal,
        textAlignVertical,
        fontRenderer,
        backgroundColor,
        outlineStroke,
        outlineColor,
        x,
        y,
        width,
        height,
        widgetColor,
        horizontalAlignment,
        verticalAlignment
    )

    override fun newInstance() = TextField()
}