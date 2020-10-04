package net.inceptioncloud.dragonfly.controls.color

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.controls.OptionControlElement
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.options.OptionKey
import net.inceptioncloud.dragonfly.overlay.modal.Modal
import net.inceptioncloud.dragonfly.utils.Either
import kotlin.reflect.KMutableProperty0

class ColorControl(
    either: Either<KMutableProperty0<out WidgetColor>, OptionKey<WidgetColor>>,
    name: String,
    description: String? = null
) : OptionControlElement<WidgetColor>(either, name, description) {

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
                Modal.showModal(ColorPickerModal(optionKey))
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
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 45, useScale = false)
            color = DragonflyPalette.background.altered { alphaDouble = 0.6 }
        }
    }

    override fun react(newValue: WidgetColor) {
        "color-preview"<ColorPreview> {
            color = optionKey.get()
        }

        "hex-code"<TextField> {
            staticText = optionKey.get().toHexString()
        }
    }
}