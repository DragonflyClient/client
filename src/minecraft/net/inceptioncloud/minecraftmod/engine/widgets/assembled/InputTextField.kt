package net.inceptioncloud.minecraftmod.engine.widgets.assembled

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.inceptioncloud.minecraftmod.Dragonfly
import net.inceptioncloud.minecraftmod.design.color.BluePalette
import net.inceptioncloud.minecraftmod.engine.animation.Animation
import net.inceptioncloud.minecraftmod.engine.animation.alter.MorphAnimation
import net.inceptioncloud.minecraftmod.engine.font.FontWeight
import net.inceptioncloud.minecraftmod.engine.font.WidgetFont
import net.inceptioncloud.minecraftmod.engine.internal.*
import net.inceptioncloud.minecraftmod.engine.internal.annotations.*
import net.inceptioncloud.minecraftmod.engine.sequence.easing.EaseCubic
import net.inceptioncloud.minecraftmod.engine.sequence.types.DoubleSequence
import net.inceptioncloud.minecraftmod.engine.structure.*
import net.inceptioncloud.minecraftmod.engine.widgets.primitive.Rectangle
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
    @property:State var isEnabled: Boolean = true,
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
    @Info
    var isFocused = false
        set(value) {
            if (field == value)
                return

            field = value
            focusedStateChanged(value)
        }

    /** The currently entered input text. */
    @Info
    var inputText: String = ""

    /** Whether the text label is raised due to present input text or focus state. */
    private val isLabelRaised: Boolean
        get() = isFocused || inputText.isNotEmpty()

    /** The position of the cursor as well as the start of the text selection*/
    private var cursorPosition: Int = 0

    /** The end of the text selection */
    private var selectionEnd: Int = 0

    /** Horizontal scroll offset. */
    private var lineScrollOffset: Int = 0

    /** The time in milliseconds the cursor has moved lately */
    private var timeCursorMoved = 0L

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

    /**
     * Builds a font renderer based on the [font], [fontSize] and [fontWeight].
     */
    private fun getFontRenderer() = (structure["input-text"] as TextField).fontRenderer

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "box-round" to RoundedRectangle(),
        "box-sharp" to Rectangle(),
        "bottom-line" to Rectangle(),
        "bottom-line-overlay" to Rectangle(),
        "label" to TextField(),
        "input-text" to TextField(),
        "cursor" to Rectangle(),
        "selection" to Rectangle()
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

        val inputText = (structure["input-text"] as TextField).also {
            it.staticText = ""
            it.font = font
            it.fontSize = fontSize
            it.fontWeight = fontWeight
            it.color = WidgetColor(Color.BLACK)
            it.width = width
            it.height = height - 2
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
            it.y = y + height - it.height
            it.color = DEFAULT_TEXT_COLOR
        }

        (structure["bottom-line-overlay"] as Rectangle).also {
            it.color = color
            it.width = 0.0
            it.height = bottomLine.height
            it.x = bottomLine.x
            it.y = bottomLine.y
        }

        (structure["cursor"] as Rectangle).also {
            it.height = inputText.fontRenderer.height.toDouble()
            it.width = 0.6
            it.color = color
            it.y = y + height - it.height - bottomLine.height - 1
        }
    }

    override fun render() {
        val fontRenderer = getFontRenderer()
        val cursorPos = cursorPosition - lineScrollOffset
        val maxStringSize = (width - padding * 2).toInt()
        val visibleText = fontRenderer.trimStringToWidth(inputText.substring(lineScrollOffset), maxStringSize)
        val end = (selectionEnd - lineScrollOffset).coerceAtMost(visibleText.length)
        val cursorInBounds = cursorPos >= 0 && cursorPos <= visibleText.length
        val cursorVisible = isFocused && ((System.currentTimeMillis() / 500) % 2 == 0L
                || System.currentTimeMillis() - timeCursorMoved < 500)
        var x1 = x

        if (visibleText.isNotEmpty()) {
            val string = if (cursorInBounds) visibleText.substring(0, cursorPos) else visibleText
            val stringWidth = fontRenderer.getStringWidth(string)
            x1 = x + stringWidth
        }

        val cursorNotAtEnd = cursorPosition < inputText.length || inputText.length >= maxStringLength
        var cursorX = x1 + padding
        val closedRange = 0..visibleText.length
        val selectionWidth: Double = fontRenderer.getStringWidth(
            visibleText.substring(
                cursorPos.coerceAtMost(end).coerceIn(closedRange), end.coerceAtLeast(cursorPos).coerceIn(closedRange)
            )
        ).toDouble()

        if (!cursorInBounds) {
            cursorX = if (cursorPos > 0) x + width else x
        } else if (cursorNotAtEnd) {
            --x1
        }

        cursorX += 0.5

        val cursor = (structure["cursor"] as Rectangle)
        val destinationCursorX = (cursor.findAnimation<MorphAnimation>()?.destination as? Rectangle)?.x
        cursor.isVisible = cursorVisible

        if (cursor.x != cursorX && destinationCursorX != cursorX) {
            timeCursorMoved = System.currentTimeMillis()
            cursor.findAnimation<MorphAnimation>()?.let { cursor.detachAnimation(it) }
            cursor.attachAnimation(
                MorphAnimation(
                    cursor.clone().apply { x = cursorX }, duration = (cursor.x.diff(cursorX) * 3).toInt().coerceAtMost(20)
                )
            ) {
                start()
                post { animation: Animation, widget: Widget<*> ->
                    widget.detachAnimation(animation)
                }
            }
        }

        (structure["input-text"] as TextField).also {
            if (it.staticText != visibleText) {
                it.staticText = visibleText
                it.updateStructure()
            }
        }

        (structure["selection"] as Rectangle).also {
            it.width = selectionWidth
            it.height = cursor.height
            it.x = if (selectionEnd < cursorPos + lineScrollOffset) cursorX - it.width else cursorX
            it.y = cursor.y
            it.color = color.clone().apply { alphaDouble = 0.5 }
            it.isVisible = end != cursorPos
        }

        super.render()
    }

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
            GuiScreen.isKeyComboCtrlX(keyCode) -> GuiScreen.clipboardString = getSelectedText()?.also { writeText("") }
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
                KEY_END -> if (isShiftKeyDown) {
                    setSelectionPos(inputText.length)
                } else {
                    setCursorPositionEnd()
                }
                KEY_LEFT, KEY_RIGHT -> {
                    val offset = if (keyCode == KEY_LEFT) -1 else +1
                    if (isShiftKeyDown) {
                        if (isCtrlKeyDown) {
                            setSelectionPos(getNthWordFromPos(offset, selectionEnd))
                        } else {
                            setSelectionPos(selectionEnd + offset)
                        }
                    } else if (isCtrlKeyDown) {
                        setCursorPosition(getNthWordFromCursor(offset))
                    } else {
                        selectionEnd = cursorPosition
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
    }

    override fun handleMousePress(data: MouseData) {
        isFocused = isHovered

        if (isFocused && data.button == 0) {
            val fontRenderer = getFontRenderer()
            val i: Int = (data.mouseX - x - padding).toInt() // TODO: -4 can be removed
            val s: String = fontRenderer.trimStringToWidth(inputText.substring(lineScrollOffset), (width - padding * 2).toInt())
            setCursorPosition(fontRenderer.trimStringToWidth(s, i).length + lineScrollOffset)
        }
    }

    override fun clone() = InputTextField(
        font, fontWeight, fontSize, padding, label, isEnabled, maxStringLength,
        x, y, width, height, color, horizontalAlignment, verticalAlignment
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

    fun writeText(newText: String, force: Boolean = false) {
        if (!isEnabled && !force)
            return

        var result = ""
        val allowedCharacters = ChatAllowedCharacters.filterAllowedCharacters(newText)

        val i = cursorPosition.coerceAtMost(selectionEnd)
        val j = cursorPosition.coerceAtLeast(selectionEnd)
        val k: Int = maxStringLength - inputText.length - (i - j)
        val l: Int

        if (inputText.isNotEmpty()) {
            result += inputText.substring(0, i)
        }

        if (k < allowedCharacters.length) {
            result += allowedCharacters.substring(0, k)
            l = k
        } else {
            result += allowedCharacters
            l = allowedCharacters.length
        }

        if (inputText.isNotEmpty() && j < inputText.length) {
            result += inputText.substring(j)
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

    fun deleteFromCursor(amount: Int, force: Boolean = false) {
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
        val maxStringWidth: Int = (width - padding * 2).toInt()
        val s: String = fontRenderer.trimStringToWidth(inputText.substring(lineScrollOffset), maxStringWidth)
        val k: Int = s.length + lineScrollOffset
        if (position == lineScrollOffset) {
            lineScrollOffset -= fontRenderer.trimStringToWidth(inputText, maxStringWidth, true).length
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

private fun Double.diff(other: Double): Double = this.coerceAtLeast(other) - other.coerceAtMost(this)
