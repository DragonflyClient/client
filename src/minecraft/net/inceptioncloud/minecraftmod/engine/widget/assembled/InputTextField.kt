package net.inceptioncloud.minecraftmod.engine.widget.assembled

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.inceptioncloud.minecraftmod.Dragonfly
import net.inceptioncloud.minecraftmod.design.color.BluePalette
import net.inceptioncloud.minecraftmod.engine.animation.alter.MorphAnimation
import net.inceptioncloud.minecraftmod.engine.font.FontWeight
import net.inceptioncloud.minecraftmod.engine.font.WidgetFont
import net.inceptioncloud.minecraftmod.engine.internal.*
import net.inceptioncloud.minecraftmod.engine.internal.annotations.Interpolate
import net.inceptioncloud.minecraftmod.engine.internal.annotations.State
import net.inceptioncloud.minecraftmod.engine.sequence.easing.EaseCubic
import net.inceptioncloud.minecraftmod.engine.sequence.types.DoubleSequence
import net.inceptioncloud.minecraftmod.engine.structure.IAlign
import net.inceptioncloud.minecraftmod.engine.structure.IColor
import net.inceptioncloud.minecraftmod.engine.structure.IDimension
import net.inceptioncloud.minecraftmod.engine.structure.IPosition
import net.inceptioncloud.minecraftmod.engine.widget.primitive.Rectangle
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiScreen.Companion.isCtrlKeyDown
import net.minecraft.client.gui.GuiScreen.Companion.isShiftKeyDown
import net.minecraft.util.ChatAllowedCharacters
import org.lwjgl.input.Keyboard.*
import java.awt.Color
import kotlin.math.abs
import kotlin.properties.Delegates

val DEFAULT_TEXT_COLOR = WidgetColor(0xababab)

class InputTextField(
    @property:State var font: WidgetFont = Dragonfly.fontDesign.defaultFont,
    @property:State var fontWeight: FontWeight = FontWeight.REGULAR,
    @property:Interpolate var fontSize: Double = 18.0,
    @property:Interpolate var padding: Double = 2.0,

    @property:State var label: String = "Input Label",
    @property:State var inputText: String = "",
    @property:State var isEnabled: Boolean = false,
    @property:State var maxStringLength: Int = 200,

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

    /**
     * Whether the text label is raised due to present input text or focus state.
     */
    private val isLabelRaised: Boolean
        get() = isFocused || inputText.isNotEmpty()

    private var selectionEnd: Int = 0
    private var cursorPosition: Int = 0
    private var lineScrollOffset: Int = 0

    init {
        val (alignedX, alignedY) = align(x, y, width, height)
        this.x = alignedX
        this.y = alignedY

        GlobalScope.launch {
            DoubleSequence(fontSize, fontSize / 1.8, 20).also {
                for (i in 0..20) {
                    it.next()
                    font.fontRenderer { size = it.current.toInt(); fontWeight = this@InputTextField.fontWeight }
                }
            }
        }
    }

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "box-round" to RoundedRectangle(),
        "box-sharp" to Rectangle(),
        "label" to TextField(),
        "input-text" to TextField(),
        "cursor" to Rectangle(),
        "bottom-line" to Rectangle(),
        "bottom-line-overlay" to Rectangle()
    )

    override fun updateStructure() {
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
            it.dynamicText = { inputText }
            it.font = font
            it.fontSize = fontSize
            it.fontWeight = fontWeight
            it.color = WidgetColor(Color.BLACK)
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
            it.fontSize = fontSize
            it.fontWeight = fontWeight
            it.color = DEFAULT_TEXT_COLOR
            it.width = width
            it.height = height
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
            it.width = 0.0
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

        label.attachAnimation(
            MorphAnimation(label.clone().also {
                it.fontSize = if (isLabelRaised) fontSize / 1.8 else fontSize
                it.height = if (isLabelRaised) height / 2.5 else height
                it.color = if (isFocused && isLabelRaised) color else DEFAULT_TEXT_COLOR
            }, 20)
        ) { start() }

        lineOverlay.attachAnimation(
            MorphAnimation(lineOverlay.clone().also {
                it.width = if (focused) width else 0.0
            }, 60, EaseCubic.IN_OUT)
        ) { start() }
    }

    private fun getFontRenderer() = font.fontRenderer { size = fontSize.toInt(); fontWeight = this@InputTextField.fontWeight }

    override fun handleKeyTyped(char: Char, keyCode: Int) {
        if (!isFocused)
            return

        when {
            GuiScreen.isKeyComboCtrlA(keyCode) -> {
                setCursorPositionEnd()
                setSelectionPos(0)
            }
            GuiScreen.isKeyComboCtrlC(keyCode) -> {
                GuiScreen.clipboardString = getSelectedText()
            }
            GuiScreen.isKeyComboCtrlV(keyCode) -> GuiScreen.clipboardString?.let { writeText(it) }
            else -> when (keyCode) {
                KEY_BACK -> if (isCtrlKeyDown) {
                    deleteWords(-1)
                } else {
                    deleteFromCursor(-1)
                }
                KEY_HOME -> if (isShiftKeyDown) {
                    setSelectionPos(0)
                } else {
                    setCursorPosition(0)
                }
                KEY_LEFT, KEY_RIGHT -> {
                    val offset = if (keyCode == KEY_LEFT) -1 else 1
                    if (isShiftKeyDown) {
                        if (isCtrlKeyDown) {
                            setSelectionPos(getNthWordFromPos(offset, selectionEnd))
                        } else {
                            setSelectionPos(selectionEnd + offset)
                        }
                    } else if (isCtrlKeyDown) {
                        setCursorPosition(getNthWordFromCursor(offset))
                    } else {
                        moveCursorBy(offset)
                    }
                }
                KEY_DELETE -> if (isCtrlKeyDown) {
                    deleteWords(1)
                } else {
                    deleteFromCursor(1)
                }
                else -> if (ChatAllowedCharacters.isAllowedCharacter(char)) {
                    writeText(char.toString())
                }
            }
        }

        if (isFocused) {
            inputText += char
        }
    }

    override fun handleMousePress(data: MouseData) {
        isFocused = isHovered
    }

    override fun clone() = InputTextField(
        font,
        fontWeight,
        fontSize,
        padding,
        label,
        inputText,
        isEnabled,
        maxStringLength,
        x,
        y,
        width,
        height,
        color,
        horizontalAlignment,
        verticalAlignment
    )

    override fun newInstance() = InputTextField()

    /* == Input Text Field Utility */

    private fun getSelectedText(): String? {
        val i = cursorPosition.coerceAtMost(selectionEnd)
        val j = cursorPosition.coerceAtLeast(selectionEnd)
        return inputText.substring(i, j)
    }

    private fun setCursorPosition(index: Int) {
        cursorPosition = index.coerceIn(0..inputText.length)
        setSelectionPos(cursorPosition)
    }

    private fun setCursorPositionEnd() = setCursorPosition(inputText.length)

    private fun moveCursorBy(amount: Int) = setCursorPosition(selectionEnd + amount)

    private fun writeText(text: String, force: Boolean = false) {
        if (!isEnabled && !force)
            return

        var result = ""
        val allowedCharacters = ChatAllowedCharacters.filterAllowedCharacters(text)

        val i = cursorPosition.coerceAtMost(selectionEnd)
        val j = cursorPosition.coerceAtLeast(selectionEnd)
        val k: Int = maxStringLength - text.length - (i - j)
        val l: Int

        if (text.isNotEmpty()) {
            result += text.substring(0, i)
        }

        if (k < allowedCharacters.length) {
            result += allowedCharacters.substring(0, k)
            l = k
        } else {
            result += allowedCharacters
            l = allowedCharacters.length
        }

        if (text.isNotEmpty() && j < text.length) {
            result += text.substring(j)
        }

        inputText = result
        moveCursorBy(i - selectionEnd + l)
    }

    private fun deleteWords(amount: Int, force: Boolean = false) {
        if (!isEnabled && !force)
            return

        if (inputText.isNotEmpty()) {
            if (selectionEnd != cursorPosition) {
                writeText("")
            } else {
                deleteFromCursor(getNthWordFromCursor(amount) - cursorPosition)
            }
        }
    }

    private fun deleteFromCursor(amount: Int, force: Boolean = false) {
        if (!isEnabled && !force)
            return

        if (inputText.isNotEmpty()) {
            if (selectionEnd != cursorPosition) {
                writeText("")
            } else {
                val toLeft: Boolean = amount < 0
                val end = if (toLeft) cursorPosition + amount else cursorPosition
                val start = if (toLeft) cursorPosition else cursorPosition + amount
                var result = ""
                if (end >= 0) {
                    result = inputText.substring(0, end)
                }
                if (start < inputText.length) {
                    result += inputText.substring(start)
                }
                inputText = result
                if (toLeft) {
                    moveCursorBy(amount)
                }
            }
        }
    }

    private fun setSelectionPos(pos: Int) {
        val i: Int = inputText.length
        val fontRenderer = getFontRenderer()
        var position = pos

        if (position > i) {
            position = i
        }

        if (position < 0) {
            position = 0
        }

        selectionEnd = position

        if (this.lineScrollOffset > i) {
            this.lineScrollOffset = i
        }
        val j: Int = width.toInt()
        val s: String = fontRenderer.trimStringToWidth(inputText.substring(lineScrollOffset), j)
        val k: Int = s.length + lineScrollOffset
        if (position == lineScrollOffset) {
            lineScrollOffset -= fontRenderer.trimStringToWidth(inputText, j, true).length
        }
        if (position > k) {
            lineScrollOffset += position - k
        } else if (position <= lineScrollOffset) {
            lineScrollOffset -= lineScrollOffset - position
        }
        lineScrollOffset = lineScrollOffset.coerceIn(0..i)
    }

    private fun getNthWordFromPos(n: Int, pos: Int): Int = getNthWordFromPosWS(n, pos, true)

    private fun getNthWordFromPosWS(n: Int, pos: Int, skipWs: Boolean): Int {
        var i = pos
        val flag = n < 0
        val j = abs(n)

        for (k in 0 until j) {
            if (!flag) {
                val l: Int = inputText.length
                i = inputText.indexOf(32.toChar(), i)
                if (i == -1) {
                    i = l
                } else {
                    while (skipWs && i < l && inputText[i].toInt() == 32) {
                        ++i
                    }
                }
            } else {
                while (skipWs && i > 0 && inputText[i - 1].toInt() == 32) {
                    --i
                }
                while (i > 0 && inputText[i - 1].toInt() != 32) {
                    --i
                }
            }
        }

        return i
    }

    private fun getNthWordFromCursor(n: Int): Int = getNthWordFromPos(n, cursorPosition)
}
