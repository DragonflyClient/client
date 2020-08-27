package net.inceptioncloud.dragonfly.apps.modmanager.controls

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.overlay.modal.Modal
import kotlin.reflect.KMutableProperty0

class ColorControl(
    property: KMutableProperty0<out WidgetColor>,
    name: String,
    description: String? = null
) : OptionControlElement<WidgetColor>(property, name, description) {

    override fun controlAssemble(): Map<String, Widget<*>> = mapOf(
        "color-preview" to ColorPreview(),
        "hex-code" to TextField()
    )

    override fun controlUpdateStructure() {
        height = height.coerceAtLeast(40.0)

        "color-preview"<ColorPreview> {
            width = 38.0
            height = 38.0
            x = controlX + controlWidth - width
            y = this@ColorControl.y + (this@ColorControl.height - height) / 2.0
            color = optionKey.get()
            clickAction = {
                Modal.showModal(ColorPickerModal())
            }
        }

        "hex-code"<TextField> {
            width = controlWidth - 50.0
            height = this@ColorControl.height
            x = controlX
            y = this@ColorControl.y
            staticText = optionKey.get().toHexString()
            textAlignVertical = Alignment.CENTER
            textAlignHorizontal = Alignment.END
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 45)
            color = DragonflyPalette.background.altered { alphaDouble = 0.6 }
        }
    }

    override fun react(newValue: WidgetColor) {
        "color-preview"<ColorPreview> {
            color = optionKey.get()
        }

        "hex-code"<TextField> {
            color = DragonflyPalette.background.altered { alphaDouble = 0.6 }
        }
    }
}