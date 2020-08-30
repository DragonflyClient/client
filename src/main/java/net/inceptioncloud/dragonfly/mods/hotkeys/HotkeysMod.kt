package net.inceptioncloud.dragonfly.mods.hotkeys

import net.inceptioncloud.dragonfly.apps.modmanager.controls.BooleanControl
import net.inceptioncloud.dragonfly.apps.modmanager.controls.ButtonControl
import net.inceptioncloud.dragonfly.apps.modmanager.controls.ControlElement
import net.inceptioncloud.dragonfly.apps.modmanager.controls.TitleControl
import net.inceptioncloud.dragonfly.mods.core.DragonflyMod
import net.inceptioncloud.dragonfly.mods.hotkeys.types.ChatHotkey
import net.inceptioncloud.dragonfly.mods.hotkeys.types.data.EnumHotkeyType
import net.inceptioncloud.dragonfly.overlay.modal.Modal

object HotkeysMod : DragonflyMod("Hotkeys") {

    @JvmStatic
    val controller = HotkeysController()

    var enabled by option(true)

    override fun publishControls(): List<ControlElement<*>> = buildList {
        add(TitleControl("General"))
        add(BooleanControl(HotkeysMod::enabled, "Enable mod"))
        add(TitleControl("Actions", "List of all hotkey actions."))
        add(ButtonControl("Add Hotkey", "Click the button to add a new Hotkey.", "Add", ::openAddPopup))
        add(TitleControl("Hotkeys", "List of all set hotkeys."))

        for(hotkey in HotkeysController().hotkeys) {
            add(hotkey.convertToButtonControl())
        }

    }

    private fun Hotkey.convertToButtonControl(): ButtonControl {

        val name = "${this.data.key} + ${if (this.data.requireAlt) "ALT" else ""} + ${if (this.data.requireCtrl) "CTRL" else ""} + ${if (this.data.requireShift) "SHIFT" else ""}"
        val type = when(this.data.type) {
            EnumHotkeyType.CHAT -> "CHAT"
        }
        val info = when(this.data.type) {
            EnumHotkeyType.CHAT -> (this.data as ChatHotkey).config.message
        }

        return ButtonControl(name, "$type | $info", "Configure") { openEditPopup(this) }
    }

    private fun openAddPopup() {
        Modal.showModal(AddHotkeyModal())
    }

    private fun openEditPopup(hotkey: Hotkey) {

    }

}