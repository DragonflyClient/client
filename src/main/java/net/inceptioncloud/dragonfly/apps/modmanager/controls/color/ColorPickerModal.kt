package net.inceptioncloud.dragonfly.apps.modmanager.controls.color

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.toWidgetColor
import net.inceptioncloud.dragonfly.engine.widgets.assembled.RoundedRectangle
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.overlay.modal.ModalWidget
import java.awt.Color

class ColorPickerModal : ModalWidget("Color Picker", 650.0, 550.0) {

    companion object {

        val flatColors = listOf(
            0xEB3B5A, 0x2D98DA, 0xFA8231, 0x3867D6, 0xF7B731, 0x8854D0, 0x20BF6B, 0xA5B1C2, 0x0FB9B1, 0x4B6584
        ).map { WidgetColor(it) }
    }

    val hue: Float
        get() = getWidget<ColorSlider>("hue-slider")!!.currentProgress / 360f
    val saturation: Float
        get() = getWidget<ColorSlider>("saturation-slider")!!.currentProgress / 100f
    val brightness: Float
        get() = getWidget<ColorSlider>("brightness-slider")!!.currentProgress / 100f
    val alpha: Double
        get() = getWidget<ColorSlider>("alpha-slider")!!.currentProgress / 100.0
    val fullColor: WidgetColor
        get() = Color.getHSBColor(hue, saturation, brightness).toWidgetColor().also { it.alphaDouble = alpha }

    override fun assemble(): Map<String, Widget<*>> {
        val map = mutableMapOf<String, Widget<*>>(
            "container" to RoundedRectangle(),
            "title" to TextField(),
            "hue-slider" to ColorSlider(),
            "saturation-slider" to ColorSlider(),
            "brightness-slider" to ColorSlider(),
            "alpha-slider" to ColorSlider(),
            "color-preview" to ColorPreview()
        )

        for (index in flatColors.indices) {
            map["preset-flat-$index"] = ColorPreview()
        }

        return map
    }

    override fun updateStructure() {
        val container = "container"<RoundedRectangle> {
            x = this@ColorPickerModal.x
            y = this@ColorPickerModal.y
            width = this@ColorPickerModal.width
            height = this@ColorPickerModal.height
            color = DragonflyPalette.background
            arc = 10.0
        }!!

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

        "saturation-slider"<ColorSlider> {
            max = 100
            colorLetter = "S"
            colorInterpolator = { Color.getHSBColor(hue, it.toFloat(), 1f) }

            x = this@ColorPickerModal.x + 100
            y = title.y + title.height + 90.0
            width = this@ColorPickerModal.width - 200
            height = 8.0
        }

        "brightness-slider"<ColorSlider> {
            max = 100
            colorLetter = "B"
            colorInterpolator = { Color.getHSBColor(hue, saturation, it.toFloat()) }

            x = this@ColorPickerModal.x + 100
            y = title.y + title.height + 130.0
            width = this@ColorPickerModal.width - 200
            height = 8.0
        }

        val alphaSlider = "alpha-slider"<ColorSlider> {
            max = 100
            currentProgress = 100
            colorLetter = "A"
            colorInterpolator = { fullColor.altered { alphaDouble = it }.base }

            x = this@ColorPickerModal.x + 100
            y = title.y + title.height + 170.0
            width = this@ColorPickerModal.width - 200
            height = 8.0
        }!!

        "color-preview"<ColorPreview> {
            x = this@ColorPickerModal.x + 65.0
            y = alphaSlider.y + 40.0
            width = 90.0
            height = 90.0
            color = fullColor
            backgroundColor = container.color
            borderSize = 3.0
        }

        updateFlatColors(alphaSlider.y + 40.0)
    }

    private fun updateFlatColors(originY: Double) {
        var currentX = x + 65.0 + 90.0 + 20.0
        for ((index, color) in flatColors.withIndex()) {
            "preset-flat-$index"<ColorPreview> {
                this.color = color
                x = currentX
                width = 40.0
                height = 40.0
                backgroundColor = DragonflyPalette.background
                borderSize = 2.0
                clickAction = { setColor(this.color) }

                if (index % 2 == 0) {
                    y = originY
                } else {
                    y = originY + 50.0
                    currentX += 50.0
                }
            }
        }
    }

    private fun setColor(color: WidgetColor) {
        val hueSlider = getWidget<ColorSlider>("hue-slider")!!
        val saturationSlider = getWidget<ColorSlider>("saturation-slider")!!
        val brightnessSlider = getWidget<ColorSlider>("brightness-slider")!!
        val alphaSlider = getWidget<ColorSlider>("alpha-slider")!!

        val hsb = Color.RGBtoHSB(color.red, color.green, color.blue, null)
        hueSlider.updateCurrent((hsb[0] * 360).toInt(), false)
        saturationSlider.updateCurrent((hsb[1] * 100).toInt(), false)
        brightnessSlider.updateCurrent((hsb[2] * 100).toInt(), false)
        alphaSlider.updateCurrent((color.alphaDouble * 100).toInt(), false)
    }

    override fun update() {
        super.update()
        "color-preview"<ColorPreview> {
            color = fullColor
        }
    }

    override fun handleMouseRelease(data: MouseData) {
        structure.values.forEach { it.handleMouseRelease(data) }
        super.handleMouseRelease(data)
    }
}