package net.inceptioncloud.minecraftmod.engine.widget.primitive

import net.inceptioncloud.minecraftmod.Dragonfly
import net.inceptioncloud.minecraftmod.engine.font.FontWeight
import net.inceptioncloud.minecraftmod.engine.font.GlyphFontRenderer
import net.inceptioncloud.minecraftmod.engine.font.IFontRenderer
import net.inceptioncloud.minecraftmod.engine.font.WidgetFont
import net.inceptioncloud.minecraftmod.engine.internal.Defaults
import net.inceptioncloud.minecraftmod.engine.internal.Widget
import net.inceptioncloud.minecraftmod.engine.internal.WidgetColor
import net.inceptioncloud.minecraftmod.engine.internal.annotations.Interpolate
import net.inceptioncloud.minecraftmod.engine.internal.annotations.State
import net.inceptioncloud.minecraftmod.engine.structure.IColor
import net.inceptioncloud.minecraftmod.engine.structure.IDimension
import net.inceptioncloud.minecraftmod.engine.structure.IPosition
import net.minecraft.client.gui.Gui
import java.awt.Color

/**
 * ## Text Renderer Widget
 *
 * A simple widget whose only purpose is to render text with the given font renderer.
 * The width and height properties are updated based on the space that the text needs.
 *
 * @param text the text to be rendered
 * @param dropShadow whether the text should have a shadow
 * @param fontRenderer the font renderer that draws the text
 */
class TextRenderer(
    @property:Interpolate var text: String = "Default Text",
    @property:Interpolate var dropShadow: Boolean = false,

    @property:State var fontRenderer: IFontRenderer = Dragonfly.fontDesign.regular,
    @property:State var font: WidgetFont? = null,
    @property:State var fontWeight: FontWeight = FontWeight.REGULAR,
    @property:Interpolate var fontSize: Double = 19.0,

    @property:State var showBounds: Boolean = false,

    @property:Interpolate override var x: Double = 0.0,
    @property:Interpolate override var y: Double = 0.0,
    @property:Interpolate override var width: Double = 0.0,
    @property:Interpolate override var height: Double = 0.0,
    @property:Interpolate override var color: WidgetColor = WidgetColor.DEFAULT
) : Widget<TextRenderer>(), IPosition, IColor, IDimension {

    override fun preRender() {
        /* kept empty since the render preparations would break the font-rendering */
    }

    override fun postRender() {
        /* kept empty for the above reason */
    }

    override fun render() {
        if (font != null) {
            fontRenderer = font?.fontRenderer {
                fontWeight = this@TextRenderer.fontWeight
                size = fontSize.toInt()
            } ?: fontRenderer
        }

        val posX = x.toFloat()
        val posY = if (fontRenderer is GlyphFontRenderer) y.toFloat() + 3F else y.toFloat()

        color.glBindColor()
        height = fontRenderer.height.toDouble()
        width = fontRenderer.drawString(text, posX, posY, color.rgb, dropShadow).toDouble() - posX

        if (showBounds) {
            Gui.drawRect(posX.toInt(), posY.toInt(), (posX + width).toInt(), (posY + height).toInt(), Color(0, 0, 0, 50).rgb)
        }
    }

    override fun stateChanged(new: Widget<*>) {
        // override to support aligning in assembled widgets
        height = fontRenderer.height.toDouble()
        width = fontRenderer.getStringWidth(text).toDouble()
    }

    /**
     * Puts a widget to the right of the text.
     */
    fun <E : Widget<E>> right(sibling: Widget<E>) {
        sibling as IPosition
        sibling.x = this.x + this.width
        sibling.y = this.y

        val (siblingWidth, _) = Defaults.getSizeOrDimension(sibling)
        @Suppress("UNCHECKED_CAST")
        Defaults.setSizeOrDimension(sibling as E, this.height, siblingWidth)
    }

    /**
     * Puts a widget below the text.
     */
    fun <E : Widget<E>> below(sibling: Widget<E>) {
        sibling as IPosition
        sibling.x = this.x
        sibling.y = this.y + this.height

        val (_, siblingHeight) = Defaults.getSizeOrDimension(sibling)
        @Suppress("UNCHECKED_CAST")
        Defaults.setSizeOrDimension(sibling as E, siblingHeight, this.width)
    }

    override fun clone() = TextRenderer(
        text, dropShadow, fontRenderer, font, fontWeight, fontSize, showBounds, x, y, width, height, color
    )

    override fun newInstance() = TextRenderer()
}