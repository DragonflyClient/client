package net.inceptioncloud.dragonfly.engine.widgets.assembled

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.engine.font.*
import net.inceptioncloud.dragonfly.engine.font.renderer.IFontRenderer
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.internal.Alignment.*
import net.inceptioncloud.dragonfly.engine.internal.annotations.Interpolate
import net.inceptioncloud.dragonfly.engine.internal.annotations.State
import net.inceptioncloud.dragonfly.engine.structure.*
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle
import net.inceptioncloud.dragonfly.engine.widgets.primitive.TextRenderer
import org.apache.logging.log4j.LogManager
import kotlin.math.floor
import kotlin.properties.Delegates

/**
 * ## Text Field Assembled Widget
 *
 * A more advanced way of rendering text. The text field provides a fixed width and height which allows supporting
 * text alignment within the bounds of the field. You are also enabled to set a background rectangle with an outline.
 * The text content can be resolved statically or dynamically while the priority is on the dynamic text.
 *
 * @property staticText a statically way to set the text
 * @property dynamicText the function to be called to dynamically change the text of the field
 * @property textAlignHorizontal the horizontal alignment of the text within the field bounds
 * @property textAlignVertical the vertical alignment of the text within the field bounds
 * @property fontRenderer the font renderer to render the text; this has no effect if a [font] is set
 * @property font the font that the text is rendered with
 * @property fontWeight the weight of the font (has no effect if no [font] is set)
 * @property fontSize the size of the font (has no effect if no [font] is set)
 * @property backgroundColor the color of the background rectangle
 * @property padding a padding between the bounds and the text
 * @property adaptHeight whether the height of the text field should be adapted to its requirements
 */
class TextField(
    initializerBlock: (TextField.() -> Unit)? = null
) : AssembledWidget<TextField>(initializerBlock), IPosition, IDimension, IColor, IAlign, IOutline {

    @Interpolate override var x: Double by property(0.0)
    @Interpolate override var y: Double by property(0.0)
    @Interpolate override var width: Double by property(50.0)
    @Interpolate override var height: Double by property(50.0)
    @Interpolate override var color: WidgetColor by property(WidgetColor.DEFAULT)
    @State override var horizontalAlignment: Alignment by property(START)
    @State override var verticalAlignment: Alignment by property(START)
    @State var adaptHeight: Boolean by property(false)

    @State var staticText: String by property("No static text set")
    @State var dynamicText: (() -> String)? by property(null)

    @State var textAlignHorizontal: Alignment by property(START)
    @State var textAlignVertical: Alignment by property(START)

    @State var fontRenderer: IFontRenderer by property(Dragonfly.fontManager.regular)
    @State var font: WidgetFont? by property(null)
    @State var fontWeight: FontWeight by property(FontWeight.REGULAR)
    @Interpolate var fontSize: Double by property(19.0)

    @Interpolate var backgroundColor: WidgetColor by property(WidgetColor(0, 0, 0, 0))
    @Interpolate var padding: Double by property(0.0)

    @Interpolate override var outlineStroke: Double by property(0.0)
    @Interpolate override var outlineColor: WidgetColor by property(WidgetColor.DEFAULT)

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

    /**
     * Performs the height-adaption.
     */
    fun adaptHeight() {
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
}
