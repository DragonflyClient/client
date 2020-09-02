package net.inceptioncloud.dragonfly.mods.hotkeys

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.apps.modmanager.controls.color.ColorPickerModal
import net.inceptioncloud.dragonfly.apps.modmanager.controls.color.ColorPreview
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
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

class AddHotkeyModal : ModalWidget("Add Hotkey", 578.0, 548.0) {

    lateinit var keySelector: KeySelector
    lateinit var shiftCheckBox: CheckBox
    lateinit var ctrlCheckBox: CheckBox
    lateinit var altCheckBox: CheckBox
    lateinit var messageTextField: InputTextField
    lateinit var timeTextField: InputTextField
    lateinit var delayTextField: InputTextField
    lateinit var fadeOutCheckBox: CheckBox
    var colorPickerValue = WidgetColor(1.0, 1.0, 1.0, 1.0)

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "container" to RoundedRectangle(),
        "title" to TextField(),
        "key-text" to TextField(),
        "key-selector" to KeySelector(),
        "shift-text" to TextField(),
        "shift-checkbox" to CheckBox(),
        "ctrl-text" to TextField(),
        "ctrl-checkbox" to CheckBox(),
        "alt-text" to TextField(),
        "alt-checkbox" to CheckBox(),
        "type-text" to TextField(),
        "type-dropdown" to TextField(),
        "message-text" to TextField(),
        "message-textfield" to InputTextField(),
        "time-text" to TextField(),
        "time-textfield" to InputTextField(),
        "delay-text" to TextField(),
        "delay-textfield" to InputTextField(),
        "fadeOut-text" to TextField(),
        "fadeOut-checkbox" to CheckBox(),
        "color-text" to TextField(),
        "color-picker" to ColorPreview(),
        "save-button" to RoundButton(),
        "cancel-button" to RoundButton()
    )

    override fun updateStructure() {
        val paddingTop = 30.0
        val padding = 50.0

        "container"<RoundedRectangle> {
            x = this@AddHotkeyModal.x
            y = this@AddHotkeyModal.y
            width = this@AddHotkeyModal.width
            height = this@AddHotkeyModal.height
            color = DragonflyPalette.background
            arc = 10.0
        }!!

        "title"<TextField> {
            x = this@AddHotkeyModal.x
            y = this@AddHotkeyModal.y + height
            width = this@AddHotkeyModal.width
            adaptHeight = true
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 60, useScale = false)
            staticText = this@AddHotkeyModal.name
            textAlignHorizontal = Alignment.CENTER
            color = DragonflyPalette.foreground
        }!!.also { it.adaptHeight() }

        "key-text"<TextField> {
            x = this@AddHotkeyModal.x + padding
            y = this@AddHotkeyModal.y + (3 * paddingTop)
            staticText = "Key"
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 48, useScale = false)
        }

        keySelector = "key-selector"<KeySelector> {
            x = this@AddHotkeyModal.x + this@AddHotkeyModal.width - 60.0 - padding
            y = this@AddHotkeyModal.y + (3 * paddingTop) - 10
            width = 60.0
            height = 40.0
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 32, useScale = false)
            blockKeys = listOf(42, 29, 56)
            clearKeys = listOf(14, 1)
        }!!

        "shift-text"<TextField> {
            x = this@AddHotkeyModal.x + padding + 10.0
            y = this@AddHotkeyModal.y + (4.5 * paddingTop)
            width = 60.0
            staticText = "Shift"
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 48, useScale = false)
        }

        shiftCheckBox = "shift-checkbox"<CheckBox> {
            x = this@AddHotkeyModal.x + this@AddHotkeyModal.width - 50.0 - padding
            y = this@AddHotkeyModal.y + (4.5 * paddingTop)
            width = 25.0
            height = 25.0
        }!!

        "ctrl-text"<TextField> {
            x = this@AddHotkeyModal.x + padding + 10.0
            y = this@AddHotkeyModal.y + (5.5 * paddingTop)
            width = 60.0
            staticText = "Ctrl"
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 48, useScale = false)
        }

        ctrlCheckBox = "ctrl-checkbox"<CheckBox> {
            x = this@AddHotkeyModal.x + this@AddHotkeyModal.width - 50.0 - padding
            y = this@AddHotkeyModal.y + (5.5 * paddingTop)
            width = 25.0
            height = 25.0
        }!!

        "alt-text"<TextField> {
            x = this@AddHotkeyModal.x + padding + 10.0
            y = this@AddHotkeyModal.y + (6.5 * paddingTop)
            width = 60.0
            staticText = "Alt"
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 48, useScale = false)
        }

        altCheckBox = "alt-checkbox"<CheckBox> {
            x = this@AddHotkeyModal.x + this@AddHotkeyModal.width - 50.0 - padding
            y = this@AddHotkeyModal.y + (6.5 * paddingTop)
            width = 25.0
            height = 25.0
        }!!

        "type-text"<TextField> {
            x = this@AddHotkeyModal.x + padding
            y = this@AddHotkeyModal.y + (9 * paddingTop)
            width = 60.0
            staticText = "Type"
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 48, useScale = false)
        }

        "type-dropdown"<TextField> {
            x = this@AddHotkeyModal.x + this@AddHotkeyModal.width - 60.0 - padding
            y = this@AddHotkeyModal.y + (9 * paddingTop)
            width = 120.0
            height = 40.0
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 42, useScale = false)
            staticText = "Chat"
        }

        "message-text"<TextField> {
            x = this@AddHotkeyModal.x + padding
            y = this@AddHotkeyModal.y + (10 * paddingTop)
            width = 120.0
            staticText = "Message"
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 48, useScale = false)
        }!!

        messageTextField = "message-textfield"<InputTextField> {
            x = this@AddHotkeyModal.x + this@AddHotkeyModal.width - 260.0 - padding
            y = this@AddHotkeyModal.y + (10 * paddingTop) - 10
            width = 250.0
            height = 30.0
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 32, useScale = false)
            label = "Message"
        }!!

        "time-text"<TextField> {
            x = this@AddHotkeyModal.x + padding
            y = this@AddHotkeyModal.y + (11 * paddingTop)
            width = 120.0
            staticText = "Time"
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 48, useScale = false)
        }

        timeTextField = "time-textfield"<InputTextField> {
            x = this@AddHotkeyModal.x + this@AddHotkeyModal.width - 110.0 - padding
            y = this@AddHotkeyModal.y + (11 * paddingTop)
            width = 100.0
            height = 30.0
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 32, useScale = false)
            label = "Duration"
            allowList = listOf(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 71, 72, 73, 75, 76, 77, 79, 80, 81, 82, 14)
            maxStringLength = 2
        }!!

        "delay-text"<TextField> {
            x = this@AddHotkeyModal.x + padding
            y = this@AddHotkeyModal.y + (12 * paddingTop)
            width = 120.0
            staticText = "Delay"
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 48, useScale = false)
        }

        delayTextField = "delay-textfield"<InputTextField> {
            x = this@AddHotkeyModal.x + this@AddHotkeyModal.width - 110.0 - padding
            y = this@AddHotkeyModal.y + (12 * paddingTop)
            width = 100.0
            height = 30.0
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 32, useScale = false)
            label = "Duration"
            allowList = listOf(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 71, 72, 73, 75, 76, 77, 79, 80, 81, 82, 14)
            maxStringLength = 2
        }!!

        "fadeOut-text"<TextField> {
            x = this@AddHotkeyModal.x + padding
            y = this@AddHotkeyModal.y + (13 * paddingTop)
            width = 300.0
            staticText = "FadeOut Animation"
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 48, useScale = false)
        }

        fadeOutCheckBox = "fadeOut-checkbox"<CheckBox> {
            x = this@AddHotkeyModal.x + this@AddHotkeyModal.width - 38.0 - padding
            y = this@AddHotkeyModal.y + (13.2 * paddingTop)
            width = 25.0
            height = 25.0
        }!!

        "color-text"<TextField> {
            x = this@AddHotkeyModal.x + padding
            y = this@AddHotkeyModal.y + (14 * paddingTop)
            width = 120.0
            staticText = "Color"
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 48, useScale = false)
        }

        "color-picker"<ColorPreview> {
            width = 25.0
            height = 25.0
            x = this@AddHotkeyModal.x + this@AddHotkeyModal.width - 38.0 - padding
            y = this@AddHotkeyModal.y + (14.2 * paddingTop)
            color = colorPickerValue
            clickAction = {
                Modal.showModal(ColorPickerModal(colorPickerValue) {
                    Modal.showModal(this@AddHotkeyModal)
                    colorPickerValue = it
                })
                Modal.hideModal()
            }
        }

        val saveButton = "save-button"<RoundButton> {
            width = 110.0
            height = 37.0
            x = this@AddHotkeyModal.x + this@AddHotkeyModal.width - width - padding
            y = this@AddHotkeyModal.y + this@AddHotkeyModal.height - height - padding + 25.0
            text = "Save"
            textSize = 50
            color = DragonflyPalette.accentNormal
            arc = 10.0
            onClick { performSave() }
        }!!

        "cancel-button"<RoundButton> {
            width = 85.0
            height = 37.0
            x = saveButton.x - width - 10.0
            y = this@AddHotkeyModal.y + this@AddHotkeyModal.height - height - padding + 25.0
            text = "Cancel"
            textSize = 50
            color = DragonflyPalette.background.brighter(0.8)
            arc = 10.0
            onClick { Modal.hideModal() }
        }

    }

    private fun performSave() {
        Toast.queue("Saving hotkey...", 100)

        if(validateForms()) {
            Modal.hideModal()

            HotkeysMod.controller.addHotkey(convertThisToHotkey())
            Minecraft.getMinecraft().currentScreen.refresh()

            Toast.queue("§aSaved hotkey!", 400)
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
        val config = ChatHotkeyConfig(messageTextField.realText)

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

    private fun validateForms(): Boolean {
        var error = false

        if (keySelector.currentText == "") {
            LogManager.getLogger().info("Error property 'Key' was not set by the user!")

            keySelector.apply {
                keySelector.morph(25, EaseQuad.IN, KeySelector::lineColor to DragonflyPalette.accentDark)?.post { animation, widget ->
                    keySelector.detachAnimation<MorphAnimation>()
                    morph(5, EaseQuad.IN, KeySelector::x to (keySelector.x - 10.0))?.post { animation, widget ->
                        keySelector.detachAnimation<MorphAnimation>()
                        keySelector.morph(5, EaseQuad.IN, KeySelector::x to (keySelector.x + 20.0))?.post { animation, widget ->
                            keySelector.detachAnimation<MorphAnimation>()
                            keySelector.morph(5, EaseQuad.IN, KeySelector::x to (keySelector.x - 20.0))?.post { animation, widget ->
                                keySelector.detachAnimation<MorphAnimation>()
                                keySelector.morph(5, EaseQuad.IN, KeySelector::x to (keySelector.x + 20.0))?.post { animation, widget ->
                                    keySelector.detachAnimation<MorphAnimation>()
                                    keySelector.morph(5, EaseQuad.IN, KeySelector::x to (keySelector.x - 10.0))?.start()
                                    keySelector.lineColor = DragonflyPalette.background.brighter(0.4)
                                }?.start()
                            }?.start()
                        }?.start()
                    }?.start()
                }?.start()
            }
            error = true
        }

        if (messageTextField.realText == "") {
            LogManager.getLogger().info("Error property 'Message' was not set by the user!")

            messageTextField.apply {
                messageTextField.morph(25, EaseQuad.IN, InputTextField::lineColor to DragonflyPalette.accentDark)?.post { animation, widget ->
                    messageTextField.detachAnimation<MorphAnimation>()
                    morph(5, EaseQuad.IN, InputTextField::x to (messageTextField.x - 10.0))?.post { animation, widget ->
                        messageTextField.detachAnimation<MorphAnimation>()
                        messageTextField.morph(5, EaseQuad.IN, InputTextField::x to (messageTextField.x + 20.0))?.post { animation, widget ->
                            messageTextField.detachAnimation<MorphAnimation>()
                            messageTextField.morph(5, EaseQuad.IN, InputTextField::x to (messageTextField.x - 20.0))?.post { animation, widget ->
                                messageTextField.detachAnimation<MorphAnimation>()
                                messageTextField.morph(5, EaseQuad.IN, InputTextField::x to (messageTextField.x + 20.0))?.post { animation, widget ->
                                    messageTextField.detachAnimation<MorphAnimation>()
                                    messageTextField.morph(5, EaseQuad.IN, InputTextField::x to (messageTextField.x - 10.0))?.start()
                                    messageTextField.lineColor = DragonflyPalette.background.brighter(0.4)
                                }?.start()
                            }?.start()
                        }?.start()
                    }?.start()
                }?.start()
            }
            error = true
        }

        if (timeTextField.realText == "") {
            LogManager.getLogger().info("Error property 'Time' was not set by the user!")

            timeTextField.apply {
                timeTextField.morph(25, EaseQuad.IN, InputTextField::lineColor to DragonflyPalette.accentDark)?.post { animation, widget ->
                    timeTextField.detachAnimation<MorphAnimation>()
                    morph(5, EaseQuad.IN, InputTextField::x to (timeTextField.x - 10.0))?.post { animation, widget ->
                        timeTextField.detachAnimation<MorphAnimation>()
                        timeTextField.morph(5, EaseQuad.IN, InputTextField::x to (timeTextField.x + 20.0))?.post { animation, widget ->
                            timeTextField.detachAnimation<MorphAnimation>()
                            timeTextField.morph(5, EaseQuad.IN, InputTextField::x to (timeTextField.x - 20.0))?.post { animation, widget ->
                                timeTextField.detachAnimation<MorphAnimation>()
                                timeTextField.morph(5, EaseQuad.IN, InputTextField::x to (timeTextField.x + 20.0))?.post { animation, widget ->
                                    timeTextField.detachAnimation<MorphAnimation>()
                                    timeTextField.morph(5, EaseQuad.IN, InputTextField::x to (timeTextField.x - 10.0))?.start()
                                    timeTextField.lineColor = DragonflyPalette.background.brighter(0.4)
                                }?.start()
                            }?.start()
                        }?.start()
                    }?.start()
                }?.start()
            }
            error = true
        }

        if (delayTextField.realText == "") {
            LogManager.getLogger().info("Error property 'Delay' was not set by the user!")

            delayTextField.apply {
                delayTextField.morph(25, EaseQuad.IN, InputTextField::lineColor to DragonflyPalette.accentDark)?.post { animation, widget ->
                    delayTextField.detachAnimation<MorphAnimation>()
                    morph(5, EaseQuad.IN, InputTextField::x to (delayTextField.x - 10.0))?.post { animation, widget ->
                        delayTextField.detachAnimation<MorphAnimation>()
                        delayTextField.morph(5, EaseQuad.IN, InputTextField::x to (delayTextField.x + 20.0))?.post { animation, widget ->
                            delayTextField.detachAnimation<MorphAnimation>()
                            delayTextField.morph(5, EaseQuad.IN, InputTextField::x to (delayTextField.x - 20.0))?.post { animation, widget ->
                                delayTextField.detachAnimation<MorphAnimation>()
                                delayTextField.morph(5, EaseQuad.IN, InputTextField::x to (delayTextField.x + 20.0))?.post { animation, widget ->
                                    delayTextField.detachAnimation<MorphAnimation>()
                                    delayTextField.morph(5, EaseQuad.IN, InputTextField::x to (delayTextField.x - 10.0))?.start()
                                    delayTextField.lineColor = DragonflyPalette.background.brighter(0.4)
                                }?.start()
                            }?.start()
                        }?.start()
                    }?.start()
                }?.start()
            }
            error = true
        }

        return !error
    }

}