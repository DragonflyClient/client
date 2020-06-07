package net.inceptioncloud.minecraftmod.engine.widget.assembled

import net.inceptioncloud.minecraftmod.Dragonfly
import net.inceptioncloud.minecraftmod.design.color.BluePalette
import net.inceptioncloud.minecraftmod.engine.animation.alter.MorphAnimation
import net.inceptioncloud.minecraftmod.engine.font.FontWeight
import net.inceptioncloud.minecraftmod.engine.font.WidgetFont
import net.inceptioncloud.minecraftmod.engine.internal.*
import net.inceptioncloud.minecraftmod.engine.internal.annotations.Interpolate
import net.inceptioncloud.minecraftmod.engine.internal.annotations.State
import net.inceptioncloud.minecraftmod.engine.sequence.easing.EaseCubic
import net.inceptioncloud.minecraftmod.engine.structure.IAlign
import net.inceptioncloud.minecraftmod.engine.structure.IColor
import net.inceptioncloud.minecraftmod.engine.structure.IDimension
import net.inceptioncloud.minecraftmod.engine.structure.IPosition
import net.inceptioncloud.minecraftmod.engine.widget.primitive.Rectangle
import java.awt.Color
import kotlin.properties.Delegates

val DEFAULT_TEXT_COLOR = WidgetColor(0xababab)

class InputTextField(
    @property:State var font: WidgetFont = Dragonfly.fontDesign.defaultFont,
    @property:State var fontWeight: FontWeight = FontWeight.REGULAR,
    @property:Interpolate var fontSize: Double = 18.0,

    @property:Interpolate var padding: Double = 2.0,

    @property:State var label: String = "Input Label",

    x: Double = 0.0,
    y: Double = 0.0,
    @property:Interpolate override var width: Double = 100.0,
    @property:Interpolate override var height: Double = 20.0,
    @property:Interpolate override var color: WidgetColor = WidgetColor(BluePalette.PRIMARY),
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
            if (field == value)
                return

            field = value
            focusedStateChanged(value)
        }

    private val isLabelRaised: Boolean
        get() = isFocused || inputText.isNotEmpty()

    @State
    var inputText: String = ""

    init {
        val (alignedX, alignedY) = align(x, y, width, height)
        this.x = alignedX
        this.y = alignedY
    }

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "box-round" to RoundedRectangle(),
        "box-sharp" to Rectangle(),
        "label" to TextField(),
        "input-text" to TextField(),
        "bottom-line" to Rectangle(),
        "bottom-line-overlay" to Rectangle()
    )

    override fun updateStructure() {
        println("Updating structure")
        val box = (structure["box-round"] as RoundedRectangle).also {
            it.x = x
            it.y = y
            it.width = width
            it.height = height
            it.arc = 2.0
            it.color = WidgetColor(0xF5F5F5)
        }

        (structure["box-sharp"] as Rectangle).also {
            it.x = x
            it.y = box.y + box.arc
            it.width = width
            it.height = box.height - box.arc
            it.color = box.color
        }

        (structure["input-text"] as TextField).also {
            it.staticText = inputText
            it.font = font
            it.fontSize = fontSize
            it.fontWeight = fontWeight
            it.color = WidgetColor(Color.BLACK.brighter())
            it.width = width
            it.height = height
            it.x = x
            it.y = y + 2
            it.padding = padding
            it.textAlignVertical = Alignment.CENTER
        }

        (structure["label"] as TextField).also {
            it.staticText = label
            it.font = font
            it.fontSize = if (isLabelRaised) fontSize / 1.8 else fontSize
            it.fontWeight = fontWeight
            it.color = if (isFocused) color else DEFAULT_TEXT_COLOR
            it.width = width
            it.height = if (isLabelRaised) height / 2.5 else height
            it.x = x
            it.y = y
            it.padding = padding
            it.textAlignVertical = Alignment.CENTER
        }

        val bottomLine = (structure["bottom-line"] as Rectangle).also {
            it.width = width
            it.height = 1.0
            it.x = x
            it.y = x + height - it.height
            it.color = DEFAULT_TEXT_COLOR
        }

        (structure["bottom-line-overlay"] as Rectangle).also {
            it.color = color
            it.width = if (isFocused) bottomLine.width else 0.0
            it.height = bottomLine.height
            it.x = bottomLine.x
            it.y = bottomLine.y
        }
    }

    /**
     * Called whenever the [isFocused] property changes.
     */
    private fun focusedStateChanged(focused: Boolean) {
        val label = structure["label"] as? TextField ?: error("Structure should contain label!")
        val lineOverlay = structure["bottom-line-overlay"] as? Rectangle
            ?: error("Structure should contain bottom line overlay!")

        if (isLabelRaised) {
            println("Raising label")
            label.attachAnimation(
                MorphAnimation(
                    label.clone().also {
                        it.fontSize = fontSize / 1.8
                        it.height = height / 2.5
                        it.color = if (isFocused) color else DEFAULT_TEXT_COLOR
                    }, duration = 20
                )
            ) { start() }
        } else {
            println("Lowering label")
            label.attachAnimation(
                MorphAnimation(
                    label.clone().also {
                        it.fontSize = fontSize
                        it.height = height
                        it.color = DEFAULT_TEXT_COLOR
                    }, duration = 20
                )
            ) { start() }
        }

        if (focused) {
            println("Gained focus")
            lineOverlay.attachAnimation(
                MorphAnimation(
                    lineOverlay.clone().also {
                        it.width = width
                    }, duration = 60, easing = EaseCubic.IN_OUT
                )
            ) { start() }
        } else {
            println("Lost focus")
            lineOverlay.attachAnimation(
                MorphAnimation(
                    lineOverlay.clone().also {
                        it.width = 0.0
                    }, duration = 60, easing = EaseCubic.IN_OUT
                )
            ) { start() }
        }
    }

    override fun handleKeyTyped(char: Char, keyCode: Int) {
        if (isFocused) {
            inputText += char
        }

        super.handleKeyTyped(char, keyCode)
    }

    override fun handleMousePress(data: MouseData) {
        isFocused = isHovered

        super.handleMousePress(data)
    }

    override fun clone() = InputTextField(
        font, fontWeight, fontSize, padding, label, x, y, width, height, color, horizontalAlignment, verticalAlignment
    )

    override fun newInstance() = InputTextField()
}