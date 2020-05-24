package net.inceptioncloud.minecraftmod.engine.widget.primitive

import net.inceptioncloud.minecraftmod.design.font.IFontRenderer
import net.inceptioncloud.minecraftmod.engine.internal.Alignment
import net.inceptioncloud.minecraftmod.engine.internal.Widget
import net.inceptioncloud.minecraftmod.engine.internal.WidgetColor
import net.inceptioncloud.minecraftmod.engine.internal.annotations.Interpolate
import net.inceptioncloud.minecraftmod.engine.internal.annotations.State
import net.inceptioncloud.minecraftmod.engine.structure.IAlign
import net.inceptioncloud.minecraftmod.engine.structure.IColor
import net.inceptioncloud.minecraftmod.engine.structure.IDimension
import net.inceptioncloud.minecraftmod.engine.structure.IPosition
import kotlin.properties.Delegates

/**
 *
 */
class TextRenderer(
    @property:Interpolate var text: String = "Sample Text",
    @property:State var fontRenderer: IFontRenderer,

    x: Double = 0.0,
    y: Double = 0.0,
    @property:Interpolate override var width: Double = 0.0,
    @property:Interpolate override var height: Double = 0.0,
    @property:Interpolate override var widgetColor: WidgetColor = WidgetColor.DEFAULT,
    @property:Interpolate override var horizontalAlignment: Alignment = Alignment.START,
    @property:Interpolate override var verticalAlignment: Alignment = Alignment.START
) : Widget<TextRenderer>(), IPosition, IDimension, IColor, IAlign {

    @Interpolate
    override var x: Double by Delegates.notNull()

    @Interpolate
    override var y: Double by Delegates.notNull()

    init {
        align(x, y, width, height)
    }

    override fun render() {
        TODO("Not yet implemented")
    }

    override fun clone() = TextRenderer(
        text = text,
        fontRenderer = fontRenderer,
        x = horizontalAlignment.reverse(x, width),
        y = verticalAlignment.reverse(y, height),
        width = width,
        height = height,
        widgetColor = widgetColor.clone(),
        horizontalAlignment = horizontalAlignment,
        verticalAlignment = verticalAlignment
    )

    override fun newInstance() = TODO("Implement this with the default font renderer")

    override fun toInfo(): Array<String> {
        TODO("Not yet implemented")
    }

    override fun align(x: Double, y: Double, width: Double, height: Double) {
        this.x = horizontalAlignment.calc(x, width)
        this.y = verticalAlignment.calc(y, height)
    }
}