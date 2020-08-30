package net.inceptioncloud.dragonfly.mods.hotkeys

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.internal.Alignment
import net.inceptioncloud.dragonfly.engine.internal.Widget
import net.inceptioncloud.dragonfly.engine.widgets.assembled.RoundedRectangle
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.overlay.modal.ModalWidget

class AddHotkeyModal : ModalWidget("Add Hotkey", 620.0, 630.0) {

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "container" to RoundedRectangle(),
        "title" to TextField()
    )

    override fun updateStructure() {

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
            y = this@AddHotkeyModal.y + padding
            width = this@AddHotkeyModal.width
            adaptHeight = true
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 60, useScale = false)
            staticText = this@AddHotkeyModal.name
            textAlignHorizontal = Alignment.CENTER
            color = DragonflyPalette.foreground
        }!!.also { it.adaptHeight() }
    }
}