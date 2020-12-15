package net.inceptioncloud.dragonfly.engine.widgets.primitive

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.engine.font.FontWeight
import net.inceptioncloud.dragonfly.engine.font.WidgetFont
import net.inceptioncloud.dragonfly.engine.font.renderer.GlyphFontRenderer
import net.inceptioncloud.dragonfly.engine.font.renderer.IFontRenderer
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.structure.*
import net.minecraft.client.gui.Gui
import java.awt.Color

/**
 * ## Text Renderer Widget
 *
 * A simple widget whose only purpose is to render text with the given font renderer.
 * The width and height properties are updated based on the space that the text needs.
 *
 * @property text the text to be rendered
 * @property dropShadow whether the text should have a shadow
 * @property fontRenderer the font renderer that draws the text
 */
class TextRenderer(
    initializerBlock: (TextRenderer.() -> Unit)? = null
) : Widget<TextRenderer>(initializerBlock), IPosition, IColor, IDimension {

    var text: String by property("Default Text")
    var showBounds: Boolean by property(false)
    var dropShadow: Boolean by property(false)
    var shadowColor: WidgetColor? by property(null)
    var shadowDistance: Double? by property(null)

    var fontRenderer: IFontRenderer? by property(null)

    override var x: Double by property(0.0)
    override var y: Double by property(0.0)
    override var width: Double = 0.0
    override var height: Double = 0.0
    override var color: WidgetColor by property(WidgetColor.DEFAULT)

    override fun preRender() {
        /* kept empty since the render preparations would break the font-rendering */
    }

    override fun postRender() {
        /* kept empty for the above reason */
    }

    override fun render() {
        if (color.alphaDouble <= 0.1)
            return

        if (fontRenderer == null)
            return

        val posX = x.toFloat()
        val posY = if (fontRenderer is GlyphFontRenderer) y.toFloat() + 3F else y.toFloat()

        color.glBindColor()
        height = fontRenderer!!.height.toDouble()
        width = if (dropShadow) {
            fontRenderer!!.drawStringWithCustomShadow(
                text, posX.toInt(), posY.toInt(), color.rgb,
                shadowColor?.rgb ?: WidgetColor(0.0, 0.0, 0.0, 0.5).rgb,
                shadowDistance?.toFloat() ?: 1F
            ).toDouble() - posX
        } else {
            fontRenderer!!.drawString(text, posX, posY, color.rgb, dropShadow).toDouble() - posX
        }

        if (showBounds) {
            Gui.drawRect(posX.toInt(), posY.toInt(), (posX + width).toInt(), (posY + height).toInt(), Color(0, 0, 0, 50).rgb)
        }
    }

    override fun stateChanged() {
        if (fontRenderer == null)
            return

        // override to support aligning in assembled widgets
        height = fontRenderer!!.height.toDouble()
        width = fontRenderer!!.getStringWidth(text).toDouble()
    }

    /**
     * Puts a widget to the right of the text.
     */
    fun <W : Widget<W>> right(sibling: Widget<W>) {
        sibling as IPosition
        sibling.x = this.x + this.width
        sibling.y = this.y

        val (siblingWidth, _) = Defaults.getSizeOrDimension(sibling)
        @Suppress("UNCHECKED_CAST")
        Defaults.setSizeOrDimension(sibling as W, this.height, siblingWidth)
    }

    /**
     * Puts a widget below the text.
     */
    fun <W : Widget<W>> below(sibling: Widget<W>) {
        sibling as IPosition
        sibling.x = this.x
        sibling.y = this.y + this.height

        val (_, siblingHeight) = Defaults.getSizeOrDimension(sibling)
        @Suppress("UNCHECKED_CAST")
        Defaults.setSizeOrDimension(sibling as W, siblingHeight, this.width)
    }
}
