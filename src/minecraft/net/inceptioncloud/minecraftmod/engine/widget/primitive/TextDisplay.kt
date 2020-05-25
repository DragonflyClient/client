package net.inceptioncloud.minecraftmod.engine.widget.primitive

import net.inceptioncloud.minecraftmod.engine.font.renderer.IFontRenderer
import net.inceptioncloud.minecraftmod.engine.internal.Widget
import net.inceptioncloud.minecraftmod.engine.internal.WidgetColor
import net.inceptioncloud.minecraftmod.engine.internal.annotations.Interpolate
import net.inceptioncloud.minecraftmod.engine.internal.annotations.State
import net.inceptioncloud.minecraftmod.engine.structure.IColor
import net.inceptioncloud.minecraftmod.engine.structure.IDimension
import net.inceptioncloud.minecraftmod.engine.structure.IPosition

class TextDisplay(
    @property:Interpolate var text: String = "Sample Text",
    @property:Interpolate var dropShadow: Boolean = false,
    @property:State var fontRenderer: IFontRenderer,

    @property:Interpolate override var x: Double = 0.0,
    @property:Interpolate override var y: Double = 0.0,
    @property:Interpolate override var width: Double = 0.0,
    @property:Interpolate override var height: Double = 0.0,
    @property:Interpolate override var widgetColor: WidgetColor = WidgetColor.DEFAULT
) : Widget<TextDisplay>(), IPosition, IDimension, IColor {

    override fun preRender() {
    }

    override fun render() {
        width = fontRenderer.drawString(text, x.toFloat(), y.toFloat(), widgetColor.rgb, dropShadow).toDouble()
        height = fontRenderer.height.toDouble()
    }

    override fun postRender() {
    }

    override fun clone() = TextDisplay(
        text = text,
        fontRenderer = fontRenderer,
        x = x,
        y = y,
        width = width,
        height = height,
        widgetColor = widgetColor.clone()
    )

    override fun newInstance() = TODO("Implement this with the default font renderer")
}