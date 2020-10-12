package net.inceptioncloud.dragonfly.engine.widgets.assembled

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.controls.keepDecimals
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.GraphicsEngine
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.animation.post
import net.inceptioncloud.dragonfly.engine.contains
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseQuad
import net.inceptioncloud.dragonfly.engine.structure.IColor
import net.inceptioncloud.dragonfly.engine.structure.IDimension
import net.inceptioncloud.dragonfly.engine.structure.IPosition
import net.inceptioncloud.dragonfly.engine.widgets.primitive.FilledCircle
import org.apache.commons.lang3.StringUtils
import org.lwjgl.input.Mouse
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class NumberSlider(
    initializerBlock: (NumberSlider.() -> Unit)? = null
): AssembledWidget<NumberSlider>(initializerBlock), IPosition, IDimension, IColor {

    override var x: Double by property(0.0)
    override var y: Double by property(0.0)
    override var width: Double by property(200.0)
    override var height: Double by property(20.0)
    override var color: WidgetColor by property(DragonflyPalette.foreground)

    var min: Double by property(1.0)
    var max: Double by property(10.0)
    var decimalPlaces: Int by property(0)
    var transformer: (Double) -> Double = { it.keepDecimals(decimalPlaces) }
    var formatter: ((String) -> String)? = null
    var liveUpdate: Boolean = false

    private val circleSize = 16.0

    private var isDragging = false

    private val symbols = DecimalFormatSymbols(Locale.ENGLISH)
    private var format = DecimalFormat("0." + StringUtils.repeat('0', decimalPlaces), symbols)
    
    var currentValue = 5.0

    var lineColor = DragonflyPalette.foreground
    var sliderInnerColor = DragonflyPalette.accentNormal
    var sliderOuterColor = DragonflyPalette.background
    var textColor = DragonflyPalette.foreground
    var textSize = 40
    var textWidth = 200.0
    var textHeight = height
    var textYSubtrahend = 0.0

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "slider-background" to RoundedRectangle(),
        "slider-foreground" to FilledCircle(),
        "slider-foreground-inner" to FilledCircle(),
        "current-value" to TextField()
    )

    override fun updateStructure() {
        format = DecimalFormat("0." + StringUtils.repeat('0', decimalPlaces), symbols)

        "slider-background"<RoundedRectangle> {
            width = this@NumberSlider.width
            height = this@NumberSlider.height
            x = this@NumberSlider.x
            y = this@NumberSlider.y + (this@NumberSlider.height - height) / 2.0
            arc = height / 2.0
            color = lineColor
        }

        "slider-foreground"<FilledCircle> {
            size = circleSize
            x = computeCircleX()
            y = this@NumberSlider.y + (this@NumberSlider.height / 2) - (size / 2)
            color = sliderOuterColor
        }

        "slider-foreground-inner"<FilledCircle> {
            size = circleSize - 4
            x = computeCircleX() + 2
            y = this@NumberSlider.y + (this@NumberSlider.height / 2) - (size / 2)
            color = sliderInnerColor
        }

        "current-value"<TextField> {
            staticText = formatString(transformer(currentValue))
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = textSize, useScale = false)
            width = textWidth
            height = textHeight
            x = this@NumberSlider.x + this@NumberSlider.width + 15.0
            y = this@NumberSlider.y - textYSubtrahend
            textAlignVertical = Alignment.CENTER
            textAlignHorizontal = Alignment.START
            color = textColor
        }
    }

    private fun computeCircleX(): Double {
        val room = (max - min)
        val progress = (currentValue.coerceIn(min..max) - min) / room
        return x + (progress * width) - (circleSize / 2)
    }

    fun react(newValue: Double) {
        if (isDragging && liveUpdate) return
        val allowDragging = isTargeted()

        "slider-foreground"<FilledCircle> {
            detachAnimation<MorphAnimation>()
            morph(20, EaseQuad.IN_OUT, FilledCircle::x to computeCircleX())
                ?.post { _, _ -> if (allowDragging && Mouse.isButtonDown(0)) isDragging = true }
                ?.start()
        }

        "slider-foreground-inner"<FilledCircle> {
            detachAnimation<MorphAnimation>()
            morph(20, EaseQuad.IN_OUT, FilledCircle::x to (computeCircleX() + 2.0))
                ?.post { _, _ -> if (allowDragging && Mouse.isButtonDown(0)) isDragging = true }
                ?.start()
        }

        "current-value"<TextField> {
            staticText = formatString(newValue)
        }
    }

    override fun update() {
        super.update()

        if (isDragging) {
            "slider-foreground"<FilledCircle> {
                x = GraphicsEngine.getMouseX().coerceIn(this@NumberSlider.x..this@NumberSlider.x + width) - circleSize / 2.0
            }
            "slider-foreground-inner"<FilledCircle> {
                x = GraphicsEngine.getMouseX().coerceIn(this@NumberSlider.x..this@NumberSlider.x + width) - circleSize / 2.0 + 2.0
            }
            "current-value"<TextField> {
                staticText = formatString(calculateMouseValue())
            }

            if (liveUpdate) updateCurrentValue()
        }
    }

    override fun handleMousePress(data: MouseData) {
        super.handleMousePress(data)

        val c = getWidget<FilledCircle>("slider-foreground")!!

        when {
            data in c -> isDragging = true
            isTargeted() -> react(updateCurrentValue())
        }
    }

    override fun handleMouseRelease(data: MouseData) {
        if (isDragging) {
            isDragging = false
            react(updateCurrentValue())
        }

        super.handleMouseRelease(data)
    }

    private fun isTargeted(): Boolean {
        val b = getWidget<RoundedRectangle>("slider-background")!!
        val mouseX = GraphicsEngine.getMouseX()
        val mouseY = GraphicsEngine.getMouseY()
        return mouseX in b.x..b.x + b.width && mouseY in b.y - 20.0..b.y + b.height + 20.0
    }

    private fun updateCurrentValue(): Double {
        val new = calculateMouseValue()
        if (new != currentValue)
            currentValue = new
        return new
    }

    private fun calculateMouseValue(): Double {
        val progress = (GraphicsEngine.getMouseX() - x) / width
        val value = (min + (progress * (max - min))).coerceIn(min..max)
        return transformer(value)
    }

    private fun formatString(value: Double): String {
        val decimalFormatted = if (decimalPlaces == 0) value.toInt().toString()
        else format.format(value)
        return formatter?.invoke(decimalFormatted) ?: decimalFormatted
    }
}