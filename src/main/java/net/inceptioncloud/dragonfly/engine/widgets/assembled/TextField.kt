package net.inceptioncloud.dragonfly.engine.widgets.assembled

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.engine.font.*
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.internal.Alignment.*
import net.inceptioncloud.dragonfly.engine.internal.annotations.Interpolate
import net.inceptioncloud.dragonfly.engine.internal.annotations.State
import net.inceptioncloud.dragonfly.engine.structure.*
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle
import net.inceptioncloud.dragonfly.engine.widgets.primitive.TextRenderer
import org.apache.logging.log4j.LogManager
import kotlin.math.floor
import kotlin.math.max
import kotlin.properties.Delegates

/**
 * ## Text Field Assembled Widget
 *
 * A more advanced way of rendering text. The text field provides a fixed width and height which allows supporting
 * text alignment within the bounds of the field. You are also enabled to set a background rectangle with an outline.
 * The text content can be resolved statically or dynamically while the priority is on the dynamic text.
 *
 * @param staticText a statically way to set the text
 * @param dynamicText the function to be called to dynamically change the text of the field
 * @param textAlignHorizontal the horizontal alignment of the text within the field bounds
 * @param textAlignVertical the vertical alignment of the text within the field bounds
 * @param fontRenderer the font renderer to render the text; this has no effect if a [font] is set
 * @param font the font that the text is rendered with
 * @param fontWeight the weight of the font (has no effect if no [font] is set)
 * @param fontSize the size of the font (has no effect if no [font] is set)
 * @param backgroundColor the color of the background rectangle
 * @param padding a padding between the bounds and the text
 */
class TextField(
    @property:State var staticText: String = "No static text set",
    @property:State var dynamicText: (() -> String)? = null,

    @property:State var textAlignHorizontal: Alignment = START,
    @property:State var textAlignVertical: Alignment = START,

    @property:State var fontRenderer: IFontRenderer = Dragonfly.fontDesign.regular,
    @property:State var font: WidgetFont? = null,
    @property:State var fontWeight: FontWeight = FontWeight.REGULAR,
    @property:Interpolate var fontSize: Double = 19.0,

    @property:Interpolate var backgroundColor: WidgetColor = WidgetColor(0, 0, 0, 0),
    @property:Interpolate var padding: Double = 0.0,

    @property:Interpolate override var outlineStroke: Double = 0.0,
    @property:Interpolate override var outlineColor: WidgetColor = WidgetColor.DEFAULT,

    x: Double = 0.0,
    y: Double = 0.0,
    @property:Interpolate override var width: Double = 50.0,
    @property:Interpolate override var height: Double = 50.0,
    @property:Interpolate override var color: WidgetColor = WidgetColor.DEFAULT,
    @property:State override var horizontalAlignment: Alignment = START,
    @property:State override var verticalAlignment: Alignment = START,
    @property:State var adaptHeight: Boolean = false
) : AssembledWidget<TextField>(), IPosition, IDimension, IColor, IAlign, IOutline {

    @Interpolate
    override var x: Double by Delegates.notNull()

    @Interpolate
    override var y: Double by Delegates.notNull()

    init {
        val (alignedX, alignedY) = align(x, y, width, height)
        this.x = alignedX
        this.y = alignedY
        adaptHeight()
    }

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "background" to Rectangle()
    )

    override fun updateStructure() {
        reassemble()
        if (font != null) {
            fontRenderer = font?.fontRenderer(
                fontWeight = this@TextField.fontWeight,
                size = fontSize.toInt()
            ) ?: fontRenderer
        }

        val maxAmount = floor((height - padding * 2) / fontRenderer.height).toInt()
        val lines = fontRenderer.listFormattedStringToWidth(currentText(), (width - padding * 2).toInt())
            .let { if (adaptHeight) it else it.take(maxAmount) }
        val size = lines.size * fontRenderer.height

        adaptHeight()

        for ((index, line) in lines.withIndex()) {
            val widget = structure["line-$index"] ?: TextRenderer().also { structure["line-$index"] = it }
            (widget as TextRenderer).also {
                it.fontRenderer = fontRenderer
                it.font = font
                it.fontSize = fontSize
                it.fontWeight = fontWeight
                it.text = line
                it.color = color
                it.x = alignText(textAlignHorizontal, x, width, fontRenderer.getStringWidth(it.text).toDouble())
                it.y = when (textAlignVertical) {
                    START -> y + index * fontRenderer.height + padding
                    CENTER -> y + (height - size) / 2 + index * fontRenderer.height
                    END -> y + height - size + index * fontRenderer.height - padding
                }
            }
        }

        structure.forEach { (key, _) ->
            if (key.startsWith("line-") && key.removePrefix("line-").toInt() >= lines.size)
                structure.remove(key)
        }

        (structure["background"] as Rectangle).also {
            it.x = x
            it.y = y
            it.width = width
            it.height = height
            it.color = backgroundColor
            it.outlineColor = outlineColor
            it.outlineStroke = outlineStroke
        }
    }

    private fun adaptHeight() {
        if (!adaptHeight)
            return

        val lines = fontRenderer.listFormattedStringToWidth(currentText(), (width - padding * 2).toInt())
        val size = lines.size * fontRenderer.height
        val previousHeight = height
        height = (size + padding * 2)

        if (verticalAlignment == END) {
            y += previousHeight - height
        }

        if (textAlignVertical == CENTER || textAlignVertical == END) {
            LogManager.getLogger().warn(
                "Using adapted height on a text field with vertical alignment of 'center' or 'end' will remove the effect of the alignment"
            )
        }
    }

    override fun update() {
        // update instantly when using dynamic text
        if (dynamicText != null) {
            updateStructure()
        }

        super.update()
    }

    /**
     * Returns the current text of the field. This is the [dynamicText] or the [staticText], if no
     * [dynamicText] is set.
     */
    private fun currentText() = dynamicText?.invoke() ?: staticText

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
            START -> coordinate + padding
            CENTER -> coordinate + (size / 2) - (textSize / 2)
            END -> coordinate + size - textSize - padding
        }

    override fun clone() = TextField(
        staticText, dynamicText, textAlignHorizontal, textAlignVertical,
        fontRenderer, font, fontWeight, fontSize,
        backgroundColor, padding, outlineStroke, outlineColor,
        x, y, width, height, color, horizontalAlignment, verticalAlignment, adaptHeight
    )

    override fun newInstance() = TextField()
}