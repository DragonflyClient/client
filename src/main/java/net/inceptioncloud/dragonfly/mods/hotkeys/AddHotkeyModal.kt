package net.inceptioncloud.dragonfly.mods.hotkeys

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.internal.Alignment
import net.inceptioncloud.dragonfly.engine.internal.Widget
import net.inceptioncloud.dragonfly.engine.widgets.assembled.*
import net.inceptioncloud.dragonfly.overlay.modal.Modal
import net.inceptioncloud.dragonfly.overlay.modal.ModalWidget
import net.inceptioncloud.dragonfly.overlay.toast.Toast

class AddHotkeyModal : ModalWidget("Add Hotkey", 578.0, 548.0) {

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "container" to RoundedRectangle(),
        "title" to TextField(),
        "key-text" to TextField(),
        "key-textfield" to InputTextField(),
        "shift-text" to TextField(),
        "shift-checkbox" to CheckBox(),
        "ctrl-text" to TextField(),
        "ctrl-checkbox" to CheckBox(),
        "alt-text" to TextField(),
        "alt-checkbox" to CheckBox(),
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

        "key-textfield"<InputTextField> {
            x = this@AddHotkeyModal.x + this@AddHotkeyModal.width - 120.0 - padding
            y = this@AddHotkeyModal.y + (3 * paddingTop) - 14
            width = 120.0
            height = 40.0
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 32, useScale = false)
            label = "Press key"
        }

        "shift-text"<TextField> {
            x = this@AddHotkeyModal.x + padding + 10.0
            y = this@AddHotkeyModal.y + (4.5 * paddingTop)
            width = 60.0
            staticText = "Shift"
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 48, useScale = false)
        }

        "shift-checkbox"<CheckBox> {
            x = this@AddHotkeyModal.x + this@AddHotkeyModal.width - 50.0 - padding
            y = this@AddHotkeyModal.y + (4.5 * paddingTop)
            width = 25.0
            height = 25.0
        }

        "ctrl-text"<TextField> {
            x = this@AddHotkeyModal.x + padding + 10.0
            y = this@AddHotkeyModal.y + (5.5 * paddingTop)
            width = 60.0
            staticText = "Ctrl"
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 48, useScale = false)
        }

        "ctrl-checkbox"<CheckBox> {
            x = this@AddHotkeyModal.x + this@AddHotkeyModal.width - 50.0 - padding
            y = this@AddHotkeyModal.y + (5.5 * paddingTop)
            width = 25.0
            height = 25.0
        }

        "alt-text"<TextField> {
            x = this@AddHotkeyModal.x + padding + 10.0
            y = this@AddHotkeyModal.y + (6.5 * paddingTop)
            width = 60.0
            staticText = "Alt"
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 48, useScale = false)
        }

        "alt-checkbox"<CheckBox> {
            x = this@AddHotkeyModal.x + this@AddHotkeyModal.width - 50.0 - padding
            y = this@AddHotkeyModal.y + (6.5 * paddingTop)
            width = 25.0
            height = 25.0
        }

        val saveButton = "save-button"<RoundButton> {
            width = 110.0
            height = 37.0
            x = this@AddHotkeyModal.x + this@AddHotkeyModal.width - width - padding
            y = this@AddHotkeyModal.y + this@AddHotkeyModal.height - height - padding
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
            y = this@AddHotkeyModal.y + this@AddHotkeyModal.height - height - padding
            text = "Cancel"
            textSize = 50
            color = DragonflyPalette.background.brighter(0.8)
            arc = 10.0
            onClick { Modal.hideModal() }
        }
        
    }

    private fun performSave() {
        Toast.queue("Saving hotkey...", 100)
        Modal.hideModal()

        Toast.queue("§aSaved hotkey!", 400)
    }

}