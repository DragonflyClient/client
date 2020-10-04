package net.inceptioncloud.dragonfly.mods.hotkeys

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.controls.color.ColorPickerModal
import net.inceptioncloud.dragonfly.controls.color.ColorPreview
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.GraphicsEngine
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.animation.post
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

class EditHotkeyModal(val originalHotkey: Hotkey) : ModalWidget("Edit Hotkey", 430.0, 440.0) {

    lateinit var keySelector: KeySelector
    lateinit var shiftCheckBox: CheckBox
    lateinit var ctrlCheckBox: CheckBox
    lateinit var altCheckBox: CheckBox
    lateinit var messageTextField: InputTextField
    lateinit var timeTextField: InputTextField
    lateinit var delayTextField: InputTextField

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
        "time-textfield" to InputTextField(),
        "delay-textfield" to InputTextField(),
        "color-picker" to ColorPreview(),
        "color-text" to TextField(),
        "send-checkbox" to CheckBox(),
        "send-text" to TextField(),
        "save-button" to RoundButton(),
        "cancel-button" to RoundButton(),
        "delete-button" to RoundButton()
    )

    override fun updateStructure() {
        val paddingTop = 30.0
        val paddingLeft = 45.0
        val padding = 20.0

        "container"<RoundedRectangle> {
            x = this@EditHotkeyModal.x
            y = this@EditHotkeyModal.y
            width = this@EditHotkeyModal.width
            height = this@EditHotkeyModal.height
            color = DragonflyPalette.background
            arc = 8.0
        }!!

        "title"<TextField> {
            x = this@EditHotkeyModal.x
            y = this@EditHotkeyModal.y + height
            width = this@EditHotkeyModal.width
            adaptHeight = true
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 64, useScale = false)
            staticText = this@EditHotkeyModal.name
            textAlignHorizontal = Alignment.CENTER
            color = DragonflyPalette.foreground
        }!!.also { it.adaptHeight() }

        keySelector = "key-selector"<KeySelector> {
            x = this@EditHotkeyModal.x + paddingLeft
            y = this@EditHotkeyModal.y + (4 * paddingTop) - 15.0
            width = 60.0
            height = 40.0
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 32, useScale = false)
            blockKeys = listOf(42, 29, 56)
            clearKeys = listOf(14, 1)
            textAlignment = Alignment.START
            lineColor = DragonflyPalette.foreground
        }!!

        shiftCheckBox = "shift-checkbox"<CheckBox> {
            x = keySelector.x + keySelector.width + (padding * 1.5)
            y = this@EditHotkeyModal.y + (4 * paddingTop)
            width = 25.0
            height = 25.0
        }!!

        val shiftText = "shift-text"<TextField> {
            x = shiftCheckBox.x + shiftCheckBox.width + 10.0
            y = shiftCheckBox.y + 2.0
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 36, useScale = false)
            staticText = "Shift"
            color = DragonflyPalette.foreground
        }!!

        ctrlCheckBox = "ctrl-checkbox"<CheckBox> {
            x = shiftText.x + shiftText.width + (padding / 2)
            y = this@EditHotkeyModal.y + (4 * paddingTop)
            width = 25.0
            height = 25.0
        }!!

        val ctrlText = "ctrl-text"<TextField> {
            x = ctrlCheckBox.x + ctrlCheckBox.width + 10.0
            y = ctrlCheckBox.y + 2.0
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 36, useScale = false)
            staticText = "Ctrl"
            color = DragonflyPalette.foreground
        }!!

        altCheckBox = "alt-checkbox"<CheckBox> {
            x = ctrlText.x + ctrlText.width + (padding / 2)
            y = this@EditHotkeyModal.y + (4 * paddingTop)
            width = 25.0
            height = 25.0
        }!!

        val altText = "alt-text"<TextField> {
            x = altCheckBox.x + altCheckBox.width + 10.0
            y = altCheckBox.y + 2.0
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 36, useScale = false)
            staticText = "Alt"
            color = DragonflyPalette.foreground
        }!!

        messageTextField = "message-textfield"<InputTextField> {
            x = this@EditHotkeyModal.x + paddingLeft
            y = this@EditHotkeyModal.y + (7 * paddingTop) - 15.0
            width = 330.0
            height = 30.0
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 38, useScale = false)
            label = "Message"
            labelScaleFactor = 0.9
            lineColor = DragonflyPalette.foreground
        }!!

        timeTextField = "time-textfield"<InputTextField> {
            x = this@EditHotkeyModal.x + paddingLeft
            y = this@EditHotkeyModal.y + (9 * paddingTop)
            width = 60.0
            height = 30.0
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 38, useScale = false)
            label = "Time"
            allowList = listOf(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 71, 72, 73, 75, 76, 77, 79, 80, 81, 82, 14, 52)
            maxStringLength = 3
            labelScaleFactor = 0.9
            lineColor = DragonflyPalette.foreground
        }!!

        delayTextField = "delay-textfield"<InputTextField> {
            x = timeTextField.x + timeTextField.width + padding
            y = this@EditHotkeyModal.y + (9 * paddingTop)
            width = 60.0
            height = 30.0
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 38, useScale = false)
            label = "Delay"
            allowList = listOf(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 71, 72, 73, 75, 76, 77, 79, 80, 81, 82, 14, 52)
            maxStringLength = 3
            labelScaleFactor = 0.9
            lineColor = DragonflyPalette.foreground
        }!!

        val colorPicker = "color-picker"<ColorPreview> {
            x = delayTextField.x + delayTextField.width + padding + 15.0
            y = this@EditHotkeyModal.y + (9 * paddingTop)
            width = 25.0
            height = 25.0
            color = colorPickerValue
            clickAction = {
                Modal.showModal(ColorPickerModal(colorPickerValue) {
                    Modal.showModal(this@EditHotkeyModal)
                    colorPickerValue = it
                })
                Modal.hideModal()
            }
        }!!

        val colorText = "color-text"<TextField> {
            x = colorPicker.x + colorPicker.width + 10.0
            y = colorPicker.y + 2.0
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 36, useScale = false)
            staticText = "Color"
            color = DragonflyPalette.foreground
        }!!

        sendInstantCheckBox = "send-checkbox"<CheckBox> {
            x = colorText.x + colorText.width + padding
            y = this@EditHotkeyModal.y + (9 * paddingTop)
            width = 25.0
            height = 25.0
        }!!

        "send-text"<TextField> {
            x = sendInstantCheckBox.x + sendInstantCheckBox.width + 10.0
            y = sendInstantCheckBox.y - 7.5
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 36, useScale = false)
            staticText = "Send instant"
            width = this.fontRenderer!!.getStringWidth("instant__").toDouble()
            color = DragonflyPalette.foreground
        }!!

        val saveButton = "save-button"<RoundButton> {
            width = 95.0
            height = 31.0
            x = this@EditHotkeyModal.x + this@EditHotkeyModal.width - width - padding
            y = this@EditHotkeyModal.y + this@EditHotkeyModal.height - height - padding
            text = "Save"
            textSize = 40
            color = DragonflyPalette.accentNormal
            arc = 2.0
            onClick { performSave() }
        }!!

        "cancel-button"<RoundButton> {
            width = 95.0
            height = 31.0
            x = saveButton.x - width - 10.0
            y = this@EditHotkeyModal.y + this@EditHotkeyModal.height - height - padding
            text = "Cancel"
            textSize = 40
            color = DragonflyPalette.background.brighter(0.8)
            arc = 2.0
            onClick { Modal.hideModal() }
        }

        "delete-button"<RoundButton> {
            width = 95.0
            height = 31.0
            x = this@EditHotkeyModal.x + (padding * 2)
            y = this@EditHotkeyModal.y + this@EditHotkeyModal.height - height - padding
            text = "Delete"
            textSize = 40
            color = DragonflyPalette.accentDark
            arc = 2.0
            onClick { performDelete() }
        }

        if (updateValuesBool) {
            readValuesFromParameter()
            updateValuesBool = false
        }

    }

    private fun performSave() {
        if (validateForms()) {
            Modal.hideModal()

            HotkeysMod.controller.removeHotkey(originalHotkey)
            HotkeysMod.controller.addHotkey(convertThisToHotkey())
            Minecraft.getMinecraft().currentScreen.refresh()
            Toast.queue("§aChanges saved!", 400)
        } else {
            Toast.queue("§cPlease check your settings!", 300)
        }
    }

    private fun performDelete() {
        Modal.hideModal()

        HotkeysMod.controller.removeHotkey(originalHotkey)

        Minecraft.getMinecraft().currentScreen.refresh()
        Toast.queue("§aDeleted hotkey!", 400)
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
            Keyboard.getKeyIndex(keySelector.currentText),
            ctrlCheckBox.isChecked,
            shiftCheckBox.isChecked,
            altCheckBox.isChecked,
            timeTextField.realText.toDouble(),
            delayTextField.realText.toDouble(),
            colorPickerValue,
            config.toJsonObject()
        )

        return ChatHotkey(data, config)
    }

    private fun readValuesFromParameter() {
        keySelector.currentText = Keyboard.getKeyName(originalHotkey.data.key)
        if (originalHotkey.data.requireShift) {
            shiftCheckBox.toggle()
        }
        if (originalHotkey.data.requireCtrl) {
            ctrlCheckBox.toggle()
        }
        if (originalHotkey.data.requireAlt) {
            altCheckBox.toggle()
        }
        if ((originalHotkey as ChatHotkey).config.sendInstant) {
            sendInstantCheckBox.toggle()
        }

        writeTextInInputTextField("message-textfield", (originalHotkey as ChatHotkey).config.message)
        writeTextInInputTextField("time-textfield", originalHotkey.data.time.toString())
        writeTextInInputTextField("delay-textfield", originalHotkey.data.delay.toString())

        colorPickerValue = originalHotkey.data.color
    }

    private fun writeTextInInputTextField(id: String, text: String) {
        getWidget<InputTextField>(id)?.run {
            writeText(text, true)
        }
    }

    private fun validateForms(): Boolean {
        var error = false

        if (keySelector.currentText == "") {
            LogManager.getLogger().info("Error property 'Key' was not set by the user!")
            keySelector.shake()
            error = true
        }

        if (messageTextField.realText == "") {
            LogManager.getLogger().info("Error property 'Message' was not set by the user!")
            messageTextField.shake()
            error = true
        }

        if (timeTextField.realText == "") {
            LogManager.getLogger().info("Error property 'Time' was not set by the user!")
            timeTextField.shake()
            error = true
        }

        if (delayTextField.realText == "") {
            LogManager.getLogger().info("Error property 'Delay' was not set by the user!")
            delayTextField.shake()
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

}