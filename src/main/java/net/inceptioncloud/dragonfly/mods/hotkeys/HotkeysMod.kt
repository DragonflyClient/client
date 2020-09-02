package net.inceptioncloud.dragonfly.mods.hotkeys

import net.inceptioncloud.dragonfly.apps.modmanager.controls.BooleanControl
import net.inceptioncloud.dragonfly.apps.modmanager.controls.ButtonControl
import net.inceptioncloud.dragonfly.apps.modmanager.controls.ControlElement
import net.inceptioncloud.dragonfly.apps.modmanager.controls.TitleControl
import net.inceptioncloud.dragonfly.mods.core.DragonflyMod
import net.inceptioncloud.dragonfly.mods.hotkeys.types.ChatHotkey
import net.inceptioncloud.dragonfly.mods.hotkeys.types.data.EnumHotkeyType
import net.inceptioncloud.dragonfly.overlay.modal.Modal
import org.lwjgl.input.Keyboard

object HotkeysMod : DragonflyMod("Hotkeys") {

    @JvmStatic
    val controller = HotkeysController

    var enabled by option(true)

    override fun publishControls(): List<ControlElement<*>> = buildList {
        add(TitleControl("General"))
        add(BooleanControl(HotkeysMod::enabled, "Enable mod"))
        add(ButtonControl("Add Hotkey", "", "Add", ::openAddPopup))
        add(TitleControl("Hotkeys", "List of all set hotkeys."))

        for(hotkey in controller.hotkeys) {
            add(hotkey.convertToButtonControl())
        }

    }

    private fun Hotkey.convertToButtonControl(): ButtonControl {

        val key = Keyboard.getKeyName(this.data.key)
        val ctrl = this.data.requireCtrl
        val shift = this.data.requireShift
        val alt = this.data.requireAlt

        val message = when(this.data.type) {
            EnumHotkeyType.CHAT -> (this as ChatHotkey).config.message
        }
        val info = "${if(ctrl) "CTRL +" else "" }${if(shift) " SHIFT +" else "" }${if(alt) " ALT +" else "" }$key"

        return ButtonControl(message.cutToNChars(16), info, "Configure") { openEditPopup(this) }
    }

    private fun openAddPopup() {
        Modal.showModal(AddHotkeyModal())
    }

    private fun openEditPopup(hotkey: Hotkey) {
        Modal.showModal(EditHotkeyModal(hotkey))
    }

    private fun String.cutToNChars(charsCount: Int): String {
        var result = this

        if(this.length > charsCount) {
            result = "${this.take(charsCount - 1)}..."
        }

        return result
    }

}