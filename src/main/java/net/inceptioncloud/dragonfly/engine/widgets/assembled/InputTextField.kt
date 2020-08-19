package net.inceptioncloud.dragonfly.engine.widgets.assembled

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.animation.post
import net.inceptioncloud.dragonfly.engine.font.FontWeight
import net.inceptioncloud.dragonfly.engine.font.WidgetFont
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.internal.annotations.Interpolate
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseCubic
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseQuad
import net.inceptioncloud.dragonfly.engine.structure.*
import net.inceptioncloud.dragonfly.engine.toWidgetColor
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiScreen.Companion.isCtrlKeyDown
import net.minecraft.client.gui.GuiScreen.Companion.isShiftKeyDown
import net.minecraft.util.ChatAllowedCharacters
import org.apache.commons.lang3.StringUtils
import org.lwjgl.input.Keyboard.*
import java.awt.Color
import kotlin.math.abs

val DEFAULT_TEXT_COLOR
        get() = DragonflyPalette.background.brighter(0.4)

/**
 * A simple text field that the user can write to. Supports common operations like copying, pasting,
 * cutting and selecting with shift + arrow key.
 *
 * @property label a short text that describes what the user should write into the field
 * @property isEnabled if the field is enabled, the user can write text to it
 * @property maxStringLength the maximum amount of characters that fit into the field
 * @property color the color that is used for the cursor and other highlighted parts of the field
 */
class InputTextField(
    initializerBlock: (InputTextField.() -> Unit)? = null
) : AssembledWidget<InputTextField>(initializerBlock), IPosition, IDimension, IAlign, IColor {

    @Interpolate override var x: Double by property(0.0)
    @Interpolate override var y: Double by property(0.0)
    @Interpolate override var width: Double by property(100.0)
    @Interpolate override var height: Double by property(20.0)
    override var horizontalAlignment: Alignment by property(Alignment.START)
    override var verticalAlignment: Alignment by property(Alignment.START)

    @Interpolate override var color: WidgetColor by property(DragonflyPalette.accentNormal)
    @Interpolate var backgroundColor: WidgetColor by property(DragonflyPalette.background)
    @Interpolate var foregroundColor: WidgetColor by property(DragonflyPalette.foreground)

    var useScale: Boolean by property(true)
    var font: WidgetFont by property(Dragonfly.fontManager.defaultFont)
    var fontWeight: FontWeight by property(FontWeight.REGULAR)
    @Interpolate var fontSize: Double by property(18.0)
    @Interpolate var padding: Double by property(2.0)

    var label: String by property("Input Label")
    var isEnabled: Boolean by property(true)
    var maxStringLength: Int by property(200)

    /**
     * Whether the text field is currently focused. If it is, typed keys will be passed on to the input field
     * and a cursor will be active. When this property changes, the [focusedStateChanged] function will be called.
     */
    var isFocused = false
        set(value) {
            if (field == value)
                return

            field = value
            focusedStateChanged(value)
        }

    /**
     * Whether the input text should be replaced with wildcards (called password-mode).
     */
    var isPassword by property(false)

    /** The currently entered input text. */
    var inputText: String = ""
        set(value) {
            realText = value
            field = if (isPassword) {
                StringUtils.repeat('*', value.length)
            } else {
                value
            }
        }

    /**
     * The real content of the input text field. This only differs from [inputText] if [isPassword]
     * is true.
     */
    var realText: String = ""

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

    private var labelHeight: Double? = null
    private var labelY: Double? = null

    init {
        val (alignedX, alignedY) = align(x, y, width, height)
        this.x = alignedX
        this.y = alignedY
    }

    /**
     * Called whenever the [isFocused] property changes.
     */
    private fun focusedStateChanged(focused: Boolean) {
        val label = structure["label"] as? TextField ?: error("Structure should contain label!")
        val lineOverlay = structure["bottom-line-overlay"] as? Rectangle
            ?: error("Structure should contain bottom line overlay!")

        label.detachAnimation<MorphAnimation>()
        label.morph(
            30, EaseQuad.IN_OUT,
            label::scaleFactor to if (isLabelRaised) 0.5 else 1.0,
            label::x to if (isLabelRaised) x + padding * 0.5 else x,
            label::y to if (isLabelRaised) y + padding * 0.5 else labelY,
            label::height to if (isLabelRaised) height / 2.5 else labelHeight,
            label::color to if (isFocused && isLabelRaised) color else DEFAULT_TEXT_COLOR
        )?.start()

        lineOverlay.detachAnimation<MorphAnimation>()
        lineOverlay.morph(
            30, EaseCubic.IN_OUT,
            lineOverlay::width to if (focused) width else 0.0
        )?.start()
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

        val inputText = (structure["input-text"] as TextField).also {
            it.staticText = ""
            it.font = font
            it.fontSize = fontSize
            it.fontWeight = fontWeight
            it.useScale = useScale
            it.color = foregroundColor
            it.width = width
            it.height = height - height / 5.0
            it.x = x
            it.y = y + height / (if (isPassword) 3.0 else 5.0)
            it.padding = padding
            it.textAlignVertical = Alignment.CENTER
        }

        (structure["label"] as TextField).also {
            it.staticText = label
            it.font = font
            it.fontSize = fontSize
            it.fontWeight = fontWeight
            it.useScale = useScale
            it.color = DEFAULT_TEXT_COLOR
            it.width = width
            it.adaptHeight = true
            it.x = x
            it.y = y + (height - it.height) / 2.0
            it.padding = padding

            if (!isLabelRaised) {
                labelHeight = it.height
                labelY = it.y
            }
        }

        val bottomLine = (structure["bottom-line"] as Rectangle).also {
            it.width = width
            it.height = height / 20.0
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
            it.width = height / 33.3
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
        val destinationCursorX = cursor.findAnimation<MorphAnimation>()?.updates?.find { it.first == cursor::x }?.second
        cursor.isVisible = cursorVisible

        if (cursor.x != cursorX && destinationCursorX != cursorX) {
            timeCursorMoved = System.currentTimeMillis()
            cursor.detachAnimation<MorphAnimation>()
            cursor.morph(
                (cursor.x.diff(cursorX) * 3).toInt().coerceAtMost(20), null,
                cursor::x to cursorX
            )?.post { animation, widget -> widget.detachAnimation(animation) }?.start()
        }

        (structure["input-text"] as TextField).also {
            if (it.staticText != visibleText) {
                it.staticText = visibleText
                it.runStructureUpdate()
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
        isFocused = data.mouseX.toDouble() in x..x + width && data.mouseY.toDouble() in y..y + height

        if (isFocused && data.button == 0) {
            val fontRenderer = getFontRenderer()
            val i: Int = (data.mouseX - x - padding).toInt() // TODO: -4 can be removed
            val s: String = fontRenderer.trimStringToWidth(inputText.substring(lineScrollOffset), (width - padding * 2).toInt())
            setCursorPosition(fontRenderer.trimStringToWidth(s, i).length + lineScrollOffset)
        }
    }

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

        if (realText.isNotEmpty()) {
            result += realText.substring(0, i)
        }

        if (k < allowedCharacters.length) {
            result += allowedCharacters.substring(0, k)
            l = k
        } else {
            result += allowedCharacters
            l = allowedCharacters.length
        }

        if (realText.isNotEmpty() && j < realText.length) {
            result += realText.substring(j)
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

        if (realText.isNotEmpty()) {
            if (selectionEnd != cursorPosition) {
                writeText("")
            } else {
                val toLeft: Boolean = amount < 0
                val end = if (toLeft) cursorPosition + amount else cursorPosition
                val start = if (toLeft) cursorPosition else cursorPosition + amount
                var result = ""
                if (end >= 0) {
                    result = realText.substring(0, end)
                }
                if (start < realText.length) {
                    result += realText.substring(start)
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
