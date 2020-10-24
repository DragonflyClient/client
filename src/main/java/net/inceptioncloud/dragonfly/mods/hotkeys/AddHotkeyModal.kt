package net.inceptioncloud.dragonfly.mods.hotkeys

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.controls.color.ColorPickerModal
import net.inceptioncloud.dragonfly.controls.color.ColorPreview
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.animation.post
import net.inceptioncloud.dragonfly.engine.font.Typography
import net.inceptioncloud.dragonfly.engine.font.font
import net.inceptioncloud.dragonfly.engine.internal.Alignment
import net.inceptioncloud.dragonfly.engine.internal.Widget
import net.inceptioncloud.dragonfly.engine.internal.WidgetColor
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseQuad
import net.inceptioncloud.dragonfly.engine.widgets.assembled.*
import net.inceptioncloud.dragonfly.mods.hotkeys.types.ChatHotkey
import net.inceptioncloud.dragonfly.mods.hotkeys.types.config.ChatHotkeyConfig
import net.inceptioncloud.dragonfly.mods.hotkeys.types.data.EnumHotkeyType
import net.inceptioncloud.dragonfly.mods.hotkeys.types.data.HotkeyData
import net.inceptioncloud.dragonfly.overlay.modal.Modal
import net.inceptioncloud.dragonfly.overlay.modal.ModalWidget
import net.inceptioncloud.dragonfly.overlay.toast.Toast
import net.minecraft.client.Minecraft
import org.apache.logging.log4j.LogManager
import org.lwjgl.input.Keyboard

class AddHotkeyModal : ModalWidget("Add Hotkey", 505.0f, 580.0f) {

    lateinit var keySelector: KeySelector
    lateinit var shiftCheckBox: CheckBox
    lateinit var ctrlCheckBox: CheckBox
    lateinit var altCheckBox: CheckBox
    lateinit var messageTextField: InputTextField
    lateinit var timeSlider: NumberSlider
    lateinit var delaySlider: NumberSlider
    lateinit var sendInstantCheckBox: CheckBox
    var colorPickerValue = WidgetColor(1.0, 1.0, 1.0, 1.0)

    var updateValuesBool = true

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "container" to RoundedRectangle(),
        "title" to TextField(),
        "key-selector" to KeySelector(),
        "shift-checkbox" to CheckBox(),
        "shift-text" to TextField(),
        "ctrl-checkbox" to CheckBox(),
        "ctrl-text" to TextField(),
        "alt-checkbox" to CheckBox(),
        "alt-text" to TextField(),
        "message-textfield" to InputTextField(),
        "time-text" to TextField(),
        "time-slider" to NumberSlider(),
        "delay-text" to TextField(),
        "delay-slider" to NumberSlider(),
        "color-picker" to ColorPreview(),
        "color-text" to TextField(),
        "send-checkbox" to CheckBox(),
        "send-text" to TextField(),
        "add-button" to RoundButton(),
        "cancel-button" to RoundButton()
    )

    override fun updateStructure() {
        val padding = 100.0f

        "container"<RoundedRectangle> {
            x = this@AddHotkeyModal.x
            y = this@AddHotkeyModal.y
            width = this@AddHotkeyModal.width
            height = this@AddHotkeyModal.height
            color = DragonflyPalette.background
            arc = 8.0f
        }!!

        "title"<TextField> {
            x = this@AddHotkeyModal.x
            y = this@AddHotkeyModal.y + (0.3f * padding)
            width = this@AddHotkeyModal.width
            adaptHeight = true
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 100)
            staticText = this@AddHotkeyModal.name
            textAlignHorizontal = Alignment.CENTER
            color = DragonflyPalette.foreground
        }!!.also { it.adaptHeight() }

        keySelector = "key-selector"<KeySelector> {
            x = this@AddHotkeyModal.x + (0.5f * padding)
            y = this@AddHotkeyModal.y + (1.2f * padding)
            width = 80.0f
            height = 55.0f
            fontRenderer = font(Typography.BASE)
            blockedKeys = listOf(1)
            exitKeys = listOf(1)
            label = "Key"
            labelScaleFactor = 0.9f
            lineColor = DragonflyPalette.foreground
            focusedLineColor = DragonflyPalette.accentNormal
            color = DragonflyPalette.accentNormal
            unfocusedLabelColor = DragonflyPalette.accentNormal
            unfocusedLabelLiftedColor = DragonflyPalette.accentNormal
            execOnFocusedChange = { updateIsCloseable(!this.isFocused) }
        }!!

        shiftCheckBox = "shift-checkbox"<CheckBox> {
            x = this@AddHotkeyModal.x + (1.8f * padding)
            y = this@AddHotkeyModal.y + (1.3f * padding)
            width = 29.0f
            height = 29.0f
        }!!

        "shift-text"<TextField> {
            x = shiftCheckBox.x + shiftCheckBox.width + 10.0f
            y = shiftCheckBox.y + 2.0f
            fontRenderer = font(Typography.BASE)
            staticText = "Shift"
            width = fontRenderer!!.getStringWidth("$staticText...").toFloat()
            color = DragonflyPalette.foreground
        }!!

        ctrlCheckBox = "ctrl-checkbox"<CheckBox> {
            x = this@AddHotkeyModal.x + (3 * padding)
            y = this@AddHotkeyModal.y + (1.3f * padding)
            width = 29.0f
            height = 29.0f
        }!!

        "ctrl-text"<TextField> {
            x = ctrlCheckBox.x + ctrlCheckBox.width + 10.0f
            y = ctrlCheckBox.y + 2.0f
            fontRenderer = font(Typography.BASE)
            staticText = "Ctrl"
            color = DragonflyPalette.foreground
        }!!

        altCheckBox = "alt-checkbox"<CheckBox> {
            x = this@AddHotkeyModal.x + (4 * padding)
            y = this@AddHotkeyModal.y + (1.3f * padding)
            width = 29.0f
            height = 29.0f
        }!!

        "alt-text"<TextField> {
            x = altCheckBox.x + altCheckBox.width + 10.0f
            y = altCheckBox.y + 2.0f
            fontRenderer = font(Typography.BASE)
            staticText = "Alt"
            color = DragonflyPalette.foreground
        }!!

        messageTextField = "message-textfield"<InputTextField> {
            x = this@AddHotkeyModal.x + (0.5f * padding)
            y = this@AddHotkeyModal.y + (2.05f * padding)
            width = 420.0f
            height = 55.0f
            fontRenderer = font(Typography.BASE)
            label = "Message"
            labelScaleFactor = 0.9f
            lineColor = DragonflyPalette.foreground
            focusedLineColor = DragonflyPalette.accentNormal
            color = DragonflyPalette.accentNormal
            unfocusedLabelColor = DragonflyPalette.accentNormal
            unfocusedLabelLiftedColor = DragonflyPalette.accentNormal
        }!!

        "time-text"<TextField> {
            x = this@AddHotkeyModal.x + (0.5f * padding)
            y = this@AddHotkeyModal.y + (3.7f * padding) - 70.0f
            fontRenderer = font(Typography.SMALL)
            staticText = "Time"
            color = DragonflyPalette.foreground
        }!!

        timeSlider = "time-slider"<NumberSlider> {
            x = this@AddHotkeyModal.x + (0.5f * padding)
            y = this@AddHotkeyModal.y + (3.7f * padding) - 30.0f
            width = 147.0f
            height = 7.0f
            decimalPlaces = 1
            min = 0.0
            max = 30.0
            lineColor = DragonflyPalette.accentNormal
            sliderInnerColor = DragonflyPalette.foreground
            textHeight = 80.0f
            textYSubtrahend = 38.0f
        }!!

        "delay-text"<TextField> {
            x = this@AddHotkeyModal.x + (2.9f * padding)
            y = this@AddHotkeyModal.y + (3.7f * padding) - 70.0f
            fontRenderer = font(Typography.SMALL)
            staticText = "Delay"
            color = DragonflyPalette.foreground
        }!!

        delaySlider = "delay-slider"<NumberSlider> {
            x = this@AddHotkeyModal.x + (2.9f * padding)
            y = this@AddHotkeyModal.y + (3.7f * padding) - 30.0f
            width = 147.0f
            height = 7.0f
            decimalPlaces = 1
            min = 0.0
            max = 30.0
            lineColor = DragonflyPalette.accentNormal
            sliderInnerColor = DragonflyPalette.foreground
            textHeight = 80.0f
            textYSubtrahend = 38.0f
        }!!

        val colorPicker = "color-picker"<ColorPreview> {
            x = this@AddHotkeyModal.x + (0.8f * padding)
            y = this@AddHotkeyModal.y + (4 * padding)
            width = 29.0f
            height = 29.0f
            color = colorPickerValue
            clickAction = {
                Modal.showModal(ColorPickerModal(colorPickerValue) {
                    Modal.showModal(this@AddHotkeyModal)
                    colorPickerValue = it
                })
                Modal.hideModal()
            }
        }!!

        "color-text"<TextField> {
            x = colorPicker.x + colorPicker.width + 15.0f
            y = colorPicker.y + 2.0f
            fontRenderer = font(Typography.BASE)
            staticText = "Color"
            width = fontRenderer!!.getStringWidth(staticText).toFloat()
            color = DragonflyPalette.foreground.altered { alphaFloat = 0.75f }
        }!!

        sendInstantCheckBox = "send-checkbox"<CheckBox> {
            x = this@AddHotkeyModal.x + (2.7f * padding)
            y = this@AddHotkeyModal.y + (4 * padding)
            width = 29.0f
            height = 29.0f
        }!!

        "send-text"<TextField> {
            x = sendInstantCheckBox.x + sendInstantCheckBox.width + 15.0f
            y = sendInstantCheckBox.y
            fontRenderer = font(Typography.BASE)
            staticText = "Send instant"
            width = 200.0f
            color = DragonflyPalette.foreground.altered { alphaFloat = 0.75f }
        }!!

        "add-button"<RoundButton> {
            width = 110.0f
            height = 40.0f
            x = this@AddHotkeyModal.x + (3.6f * padding)
            y = this@AddHotkeyModal.y + (5 * padding)
            text = "Add"
            textSize = 50
            color = DragonflyPalette.accentNormal
            arc = 3.0f
            onClick { performAdd() }
        }!!

        "cancel-button"<RoundButton> {
            width = 110.0f
            height = 40.0f
            x = this@AddHotkeyModal.x + (2.4f * padding)
            y = this@AddHotkeyModal.y + (5 * padding)
            text = "Cancel"
            textSize = 50
            color = DragonflyPalette.background.brighter(0.8)
            arc = 3.0f
            onClick { Modal.hideModal() }
        }

        if (updateValuesBool) {
            updateValuesBool = false
        }

    }

    private fun performAdd() {
        if (validateForms()) {
            Modal.hideModal()

            HotkeysMod.controller.addHotkey(convertThisToHotkey())
            Minecraft.getMinecraft().currentScreen.refresh()
            Toast.queue("§aChanges saved!", 400)
        } else {
            Toast.queue("§cPlease check your settings!", 300)
        }
    }

    override fun handleKeyTyped(char: Char, keyCode: Int) {
        super.handleKeyTyped(char, keyCode)

        structure.values.forEach {
            it.handleKeyTyped(char, keyCode)
        }

    }

    private fun convertThisToHotkey(): Hotkey {
        val config = ChatHotkeyConfig(messageTextField.realText, sendInstantCheckBox.isChecked)

        val data = HotkeyData(
            EnumHotkeyType.CHAT,
            Keyboard.getKeyIndex(keySelector.inputText),
            ctrlCheckBox.isChecked,
            shiftCheckBox.isChecked,
            altCheckBox.isChecked,
            timeSlider.currentValue,
            delaySlider.currentValue,
            colorPickerValue,
            config.toJsonObject()
        )

        return ChatHotkey(data, config)
    }

    private fun validateForms(): Boolean {
        var error = false

        if (keySelector.inputText == "") {
            LogManager.getLogger().info("Error property 'Key' was not set by the user!")
            keySelector.shake()
            error = true
        }

        if (messageTextField.realText == "") {
            LogManager.getLogger().info("Error property 'Message' was not set by the user!")
            messageTextField.shake()
            error = true
        }

        return !error
    }

    private fun InputTextField.shake() {
        this.apply {
            morph(25, EaseQuad.IN, InputTextField::lineColor to DragonflyPalette.accentDark)?.post { _, _ ->
                detachAnimation<MorphAnimation>()
                morph(5, EaseQuad.IN, InputTextField::x to (this.x - 10.0))?.post { _, _ ->
                    detachAnimation<MorphAnimation>()
                    morph(5, EaseQuad.IN, InputTextField::x to (this.x + 20.0))?.post { _, _ ->
                        detachAnimation<MorphAnimation>()
                        morph(5, EaseQuad.IN, InputTextField::x to (this.x - 20.0))?.post { _, _ ->
                            detachAnimation<MorphAnimation>()
                            morph(5, EaseQuad.IN, InputTextField::x to (this.x + 20.0))?.post { _, _ ->
                                detachAnimation<MorphAnimation>()
                                morph(5, EaseQuad.IN, InputTextField::x to (this.x - 10.0))?.start()
                                lineColor = DragonflyPalette.background.brighter(0.4)
                            }?.start()
                        }?.start()
                    }?.start()
                }?.start()
            }?.start()
        }
    }

    private fun KeySelector.shake() {
        this.apply {
            morph(25, EaseQuad.IN, KeySelector::lineColor to DragonflyPalette.accentDark)?.post { _, _ ->
                detachAnimation<MorphAnimation>()
                morph(5, EaseQuad.IN, KeySelector::x to (this.x - 10.0))?.post { _, _ ->
                    detachAnimation<MorphAnimation>()
                    morph(5, EaseQuad.IN, KeySelector::x to (this.x + 20.0))?.post { _, _ ->
                        detachAnimation<MorphAnimation>()
                        morph(5, EaseQuad.IN, KeySelector::x to (this.x - 20.0))?.post { _, _ ->
                            detachAnimation<MorphAnimation>()
                            morph(5, EaseQuad.IN, KeySelector::x to (this.x + 20.0))?.post { _, _ ->
                                detachAnimation<MorphAnimation>()
                                morph(5, EaseQuad.IN, KeySelector::x to (this.x - 10.0))?.start()
                                lineColor = DragonflyPalette.background.brighter(0.4)
                            }?.start()
                        }?.start()
                    }?.start()
                }?.start()
            }?.start()
        }
    }

    private fun updateIsCloseable(value: Boolean) {
        this@AddHotkeyModal.isCloseable = value
    }

}