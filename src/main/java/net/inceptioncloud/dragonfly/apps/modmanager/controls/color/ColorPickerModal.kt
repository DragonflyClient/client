package net.inceptioncloud.dragonfly.apps.modmanager.controls.color

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.widgets.assembled.*
import net.inceptioncloud.dragonfly.overlay.modal.ModalWidget
import java.awt.Color

class ColorPickerModal : ModalWidget("Color Picker", 650.0, 550.0) {

    companion object {

        val flatColors = listOf(
            0xEB3B5A, 0x2D98DA, 0xFA8231, 0x3867D6, 0xF7B731, 0x8854D0, 0x20BF6B, 0xA5B1C2, 0x0FB9B1, 0x4B6584
        ).map { WidgetColor(it) }

        val dragonflyColors = with(DragonflyPalette) {
            listOf(accentDark, background, accentNormal, foreground, accentBright)
        }
    }

    val rainbow: Boolean
        get() = getWidget<RoundToggleButton>("rainbow-toggle")!!.isToggled
    val hue: Float
        get() = if (rainbow) WidgetColor.getRainbowHue() else getWidget<ColorSlider>("hue-slider")!!.currentProgress / 360f
    val saturation: Float
        get() = getWidget<ColorSlider>("saturation-slider")!!.currentProgress / 100f
    val brightness: Float
        get() = getWidget<ColorSlider>("brightness-slider")!!.currentProgress / 100f
    val alpha: Double
        get() = getWidget<ColorSlider>("alpha-slider")!!.currentProgress / 100.0
    val fullColor: WidgetColor
        get() = WidgetColor(hue, saturation, brightness, (alpha * 255).toInt()).also { it.rainbow = rainbow }

    override fun assemble(): Map<String, Widget<*>> {
        val map = mutableMapOf<String, Widget<*>>(
            "container" to RoundedRectangle(),
            "title" to TextField(),
            "hue-slider" to ColorSlider(),
            "saturation-slider" to ColorSlider(),
            "brightness-slider" to ColorSlider(),
            "alpha-slider" to ColorSlider(),
            "color-preview" to ColorPreview(),
            "confirm-button" to RoundButton(),
            "reset-button" to RoundButton(),
            "rainbow-toggle" to RoundToggleButton()
        )

        for (index in flatColors.indices) {
            map["preset-flat-$index"] = ColorPreview()
        }
        for (index in dragonflyColors.indices) {
            map["preset-drgn-$index"] = ColorPreview()
        }

        return map
    }

    override fun updateStructure() {
        val sliderWidth = this@ColorPickerModal.width - 200
        val actualSliderWidth = sliderWidth + 110.0
        val contentX = x + (width / 2) - (actualSliderWidth / 2)
        val sliderX = contentX + 50.0
        val padding = contentX - x
        val sliderDistance = 40.0

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
            y = this@ColorPickerModal.y + padding
            width = this@ColorPickerModal.width
            adaptHeight = true
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 60, useScale = false)
            staticText = this@ColorPickerModal.name
            textAlignHorizontal = Alignment.CENTER
            color = DragonflyPalette.foreground
        }!!.also { it.adaptHeight() }

        val hueSlider = "hue-slider"<ColorSlider> {
            x = sliderX
            y = title.y + title.height + 45.0
            width = this@ColorPickerModal.width - 200
            height = 8.0
        }!!

        val saturationSlider = "saturation-slider"<ColorSlider> {
            max = 100
            colorLetter = "S"
            colorInterpolator = { Color.getHSBColor(hue, it.toFloat(), 1f) }

            x = sliderX
            y = hueSlider.y + sliderDistance
            width = this@ColorPickerModal.width - 200
            height = 8.0
        }!!

        val brightnessSlider = "brightness-slider"<ColorSlider> {
            max = 100
            colorLetter = "B"
            colorInterpolator = { Color.getHSBColor(hue, saturation, it.toFloat()) }

            x = sliderX
            y = saturationSlider.y + sliderDistance
            width = this@ColorPickerModal.width - 200
            height = 8.0
        }!!

        val alphaSlider = "alpha-slider"<ColorSlider> {
            max = 100
            currentProgress = 100
            colorLetter = "A"
            colorInterpolator = { fullColor.altered { alphaDouble = it }.base }

            x = sliderX
            y = brightnessSlider.y + sliderDistance
            width = this@ColorPickerModal.width - 200
            height = 8.0
        }!!

        "color-preview"<ColorPreview> {
            x = contentX
            y = alphaSlider.y + 60.0
            width = 90.0
            height = 90.0
            color = fullColor
            backgroundColor = container.color
            borderSize = 3.0
        }

        "rainbow-toggle"<RoundToggleButton> {
            width = 115.0
            height = 37.0
            x = contentX
            y = this@ColorPickerModal.y + this@ColorPickerModal.height - height - padding
            text = "Rainbow"
            textSize = 50
            color = DragonflyPalette.background.brighter(0.8)
            toggleTextColor = color
            toggleColor = DragonflyPalette.foreground
            arc = 10.0
            onClick {
                if (isToggled) setColor(WidgetColor(1.0, 0.0, 0.0, 1.0))
            }
        }

        val confirmButton = "confirm-button"<RoundButton> {
            width = 110.0
            height = 37.0
            x = this@ColorPickerModal.x + this@ColorPickerModal.width - padding - width
            y = this@ColorPickerModal.y + this@ColorPickerModal.height - height - padding
            text = "Confirm"
            textSize = 50
            color = DragonflyPalette.accentNormal
            arc = 10.0
            onClick {
                println("Confirm the color here")
            }
        }!!

        "reset-button"<RoundButton> {
            width = 100.0
            height = 37.0
            x = confirmButton.x - width - 10.0
            y = this@ColorPickerModal.y + this@ColorPickerModal.height - height - padding
            text = "Reset"
            textSize = 50
            color = DragonflyPalette.background.brighter(0.8)
            arc = 10.0
            onClick {
                println("Reset the color here")
            }
        }

        updateFlatColors(contentX, alphaSlider.y + 60.0)
        updateDragonflyColors(padding, alphaSlider.y + 60.0)
    }

    private fun updateFlatColors(contentX: Double, originY: Double) {
        var currentX = contentX + 90.0 + 20.0
        for ((index, color) in flatColors.withIndex()) {
            "preset-flat-$index"<ColorPreview> {
                this.color = color
                x = currentX
                width = 40.0
                height = 40.0
                backgroundColor = DragonflyPalette.background
                borderSize = 2.0
                clickAction = {
                    setRainbow(false)
                    setColor(this.color)
                }

                if (index % 2 == 0) {
                    y = originY
                } else {
                    y = originY + 50.0
                    currentX += 50.0
                }
            }
        }
    }

    private fun updateDragonflyColors(padding: Double, originY: Double) {
        var currentX = x + width - padding - 40.0
        for ((index, color) in dragonflyColors.withIndex()) {
            "preset-drgn-$index"<ColorPreview> {
                this.color = color
                x = currentX
                width = 40.0
                height = 40.0
                backgroundColor = DragonflyPalette.background
                borderSize = 2.0
                clickAction = {
                    setRainbow(false)
                    setColor(this.color)
                }

                if (index % 2 == 0) {
                    y = originY + 50.0
                } else {
                    y = originY
                    currentX -= 50.0
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

    /**
     * Sets whether [rainbow][WidgetColor.rainbow] is enabled for the [WidgetColor].
     */
    private fun setRainbow(rainbow: Boolean) {
        val rainbowToggle = getWidget<RoundToggleButton>("rainbow-toggle")!!
        if (rainbowToggle.isToggled != rainbow) rainbowToggle.toggle()
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