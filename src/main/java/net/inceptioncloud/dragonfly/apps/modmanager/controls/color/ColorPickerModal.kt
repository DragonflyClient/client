package net.inceptioncloud.dragonfly.apps.modmanager.controls.color

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.widgets.assembled.RoundedRectangle
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.overlay.modal.ModalWidget

class ColorPickerModal : ModalWidget("Color Picker", 650.0, 550.0) {

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "container" to RoundedRectangle(),
        "title" to TextField(),
        "hue-slider" to ColorSlider()
    )

    override fun updateStructure() {
        "container"<RoundedRectangle> {
            x = this@ColorPickerModal.x
            y = this@ColorPickerModal.y
            width = this@ColorPickerModal.width
            height = this@ColorPickerModal.height
            color = DragonflyPalette.background
            arc = 10.0
        }

        val title = "title"<TextField> {
            x = this@ColorPickerModal.x
            y = this@ColorPickerModal.y + 35.0
            width = this@ColorPickerModal.width
            adaptHeight = true
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 60, useScale = false)
            staticText = this@ColorPickerModal.name
            textAlignHorizontal = Alignment.CENTER
            color = DragonflyPalette.foreground
        }!!.also { it.adaptHeight() }

        "hue-slider"<ColorSlider> {
            x = this@ColorPickerModal.x + 100
            y = title.y + title.height + 50.0
            width = this@ColorPickerModal.width - 200
            height = 8.0
        }
    }

    override fun handleMouseRelease(data: MouseData) {
        structure.values.forEach { it.handleMouseRelease(data) }
        super.handleMouseRelease(data)
    }
}