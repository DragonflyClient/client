package net.inceptioncloud.dragonfly.engine.widgets.assembled

import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.font.renderer.IFontRenderer
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.internal.annotations.Interpolate
import net.inceptioncloud.dragonfly.engine.structure.IAlign
import net.inceptioncloud.dragonfly.engine.structure.IColor
import net.inceptioncloud.dragonfly.engine.structure.IDimension
import net.inceptioncloud.dragonfly.engine.structure.IPosition
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle
import org.lwjgl.input.Keyboard

class KeySelector(
    initializerBlock: (KeySelector.() -> Unit)? = null
) : AssembledWidget<KeySelector>(initializerBlock), IPosition, IDimension, IAlign, IColor {

    @Interpolate override var x: Double by property(0.0)
    override var y: Double by property(0.0)
    override var width: Double by property(100.0)
    override var height: Double by property(20.0)
    override var horizontalAlignment: Alignment by property(Alignment.START)
    override var verticalAlignment: Alignment by property(Alignment.START)

    override var color: WidgetColor by property(DragonflyPalette.accentNormal)
    var backgroundColor: WidgetColor by property(DragonflyPalette.background)
    var foregroundColor: WidgetColor by property(DragonflyPalette.foreground)
    @Interpolate var lineColor: WidgetColor by property(DragonflyPalette.background.brighter(0.4))

    var fontRenderer: IFontRenderer? by property(null)

    @Interpolate
    var padding: Double by property(2.0)

    var inputText: TextField? = null
    var isFocused = false
    var currentText = ""

    var blockKeys = listOf<Int>()
    var clearKeys = listOf<Int>(14, 1)

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "box-round" to RoundedRectangle(),
        "box-sharp" to Rectangle(),
        "bottom-line" to Rectangle(),
        "bottom-line-overlay" to Rectangle(),
        "input-text" to TextField()
    )

    override fun updateStructure() {

        val box = (structure["box-round"] as RoundedRectangle).also {
            it.x = x
            it.y = y
            it.width = width
            it.height = height
            it.arc = width / 100.0
            it.color = backgroundColor
        }

        (structure["box-sharp"] as Rectangle).also {
            it.x = x
            it.y = box.y + box.arc
            it.width = width
            it.height = box.height - box.arc
            it.color = box.color
        }

        inputText = (structure["input-text"] as TextField).also {
            it.staticText = currentText
            it.fontRenderer = fontRenderer
            it.color = foregroundColor
            it.width = width
            it.height = height - height / 5.0
            it.x = x
            it.y = y + height / 5.0
            it.padding = padding
            it.textAlignVertical = Alignment.CENTER
            it.textAlignHorizontal = Alignment.CENTER
        }

        val bottomLine = (structure["bottom-line"] as Rectangle).also {
            it.width = width
            it.height = height / 20.0
            it.x = x
            it.y = y + height - it.height
            it.color = lineColor
        }

        (structure["bottom-line-overlay"] as Rectangle).also {
            it.color = color
            it.width = 0.0
            it.height = bottomLine.height
            it.x = bottomLine.x
            it.y = bottomLine.y
        }

    }

    override fun handleKeyTyped(char: Char, keyCode: Int) {
        if (isFocused) {

            if (inputText == null || blockKeys.contains(keyCode))
                return

            if (clearKeys.contains(keyCode)) {
                currentText = ""
                inputText?.staticText = currentText
            } else {
                currentText = Keyboard.getKeyName(keyCode)
                inputText?.staticText = currentText
            }

        }
    }

    override fun handleMousePress(data: MouseData) {
        isFocused = data.mouseX.toDouble() in x..x + width && data.mouseY.toDouble() in y..y + height

        lineColor = if(isFocused) {
            DragonflyPalette.accentNormal
        }else {
            DragonflyPalette.background.brighter(0.4)
        }

    }

}