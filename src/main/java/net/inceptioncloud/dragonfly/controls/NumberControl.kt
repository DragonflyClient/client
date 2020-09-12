package net.inceptioncloud.dragonfly.controls

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.GraphicsEngine
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.animation.post
import net.inceptioncloud.dragonfly.engine.contains
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseQuad
import net.inceptioncloud.dragonfly.engine.widgets.assembled.RoundedRectangle
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.engine.widgets.primitive.FilledCircle
import net.inceptioncloud.dragonfly.options.OptionKey
import net.inceptioncloud.dragonfly.utils.Either
import org.apache.commons.lang3.StringUtils
import org.lwjgl.input.Mouse
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.reflect.KMutableProperty0

class NumberControl(
    either: Either<KMutableProperty0<out Number>, OptionKey<Number>>,
    name: String,
    description: String? = null,
    val min: Double,
    val max: Double,
    val decimalPlaces: Int = 0,
    val transformer: (Number) -> Number = { it.keepDecimals(decimalPlaces) },
    val formatter: ((String) -> String)? = null,
    val liveUpdate: Boolean = false
) : OptionControlElement<Number>(either, name, description) {

    constructor(
        property: KMutableProperty0<out Number>,
        name: String,
        description: String? = null,
        min: Double,
        max: Double,
        decimalPlaces: Int = 0,
        transformer: (Number) -> Number = { it.keepDecimals(decimalPlaces) },
        formatter: ((String) -> String)? = null,
        liveUpdate: Boolean = false
    ) : this(Either(a = property), name, description, min, max, decimalPlaces, transformer, formatter, liveUpdate)

    private val sliderWidth by lazy { controlWidth / 1.5 }
    private val sliderX by lazy { x + width - sliderWidth }
    private val sliderHeight = 6.0
    private val circleSize = 16.0

    private var isDragging = false

    private val symbols = DecimalFormatSymbols(Locale.ENGLISH)
    private val format = DecimalFormat("0." + StringUtils.repeat('0', decimalPlaces), symbols)

    override fun controlAssemble(): Map<String, Widget<*>> = mapOf(
        "slider-background" to RoundedRectangle(),
        "slider-foreground" to FilledCircle(),
        "current-value" to TextField()
    )

    override fun controlUpdateStructure() {
        "slider-background"<RoundedRectangle> {
            width = sliderWidth
            height = sliderHeight
            x = sliderX
            y = this@NumberControl.y + (this@NumberControl.height - height) / 2.0
            arc = height / 2.0
            color = WidgetColor(230, 230, 230)
        }

        "slider-foreground"<FilledCircle> {
            size = circleSize
            x = computeCircleX()
            y = this@NumberControl.y + (this@NumberControl.height - size) / 2.0
            color = DragonflyPalette.accentNormal
        }

        "current-value"<TextField> {
            width = controlWidth / 3.0 - 15.0
            height = this@NumberControl.height
            x = sliderX - width - 15.0
            y = this@NumberControl.y
            staticText = formatString(transformer(optionKey.get()))
            textAlignVertical = Alignment.CENTER
            textAlignHorizontal = Alignment.END
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 45, useScale = false)
            color = DragonflyPalette.background.altered { alphaDouble = 0.6 }
        }
    }

    private fun computeCircleX(): Double {
        val room = (max - min)
        val progress = (optionKey.get().toDouble().coerceIn(min..max) - min) / room
        return sliderX + (progress * sliderWidth) - (circleSize / 2)
    }

    override fun react(newValue: Number) {
        if (isDragging && liveUpdate) return

        "slider-foreground"<FilledCircle> {
            detachAnimation<MorphAnimation>()
            morph(20, EaseQuad.IN_OUT, FilledCircle::x to computeCircleX())
                ?.post { _, _ -> if (Mouse.isButtonDown(0)) isDragging = true }
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
                x = GraphicsEngine.getMouseX().coerceIn(sliderX..sliderX + sliderWidth) - circleSize / 2.0
            }
            "current-value"<TextField> {
                staticText = formatString(calculateMouseValue())
            }

            if (liveUpdate) updateOptionKeyValue()
        }
    }

    override fun handleMousePress(data: MouseData) {
        val c = getWidget<FilledCircle>("slider-foreground")!!
        val b = getWidget<RoundedRectangle>("slider-background")!!

        val mouseX = data.mouseX.toDouble()
        val mouseY = data.mouseY.toDouble()

        when {
            data in c -> isDragging = true
            mouseX in b.x..b.x + b.width && mouseY in b.y - 20.0..b.y + b.height + 20.0 -> updateOptionKeyValue()
        }

        super.handleMousePress(data)
    }

    override fun handleMouseRelease(data: MouseData) {
        if (isDragging) {
            isDragging = false
            react(updateOptionKeyValue())
        }

        super.handleMouseRelease(data)
    }

    private fun updateOptionKeyValue(): Number {
        val new = calculateMouseValue()
        if (new != optionKey.get())
            optionKey.set(new)
        return new
    }

    private fun calculateMouseValue(): Number {
        val progress = (GraphicsEngine.getMouseX() - sliderX) / sliderWidth
        val value = (min + (progress * (max - min))).coerceIn(min..max)
        return transformer(value)
    }

    private fun formatString(value: Number): String {
        val decimalFormatted = if (decimalPlaces == 0) value.toInt().toString()
        else format.format(value)
        return formatter?.invoke(decimalFormatted) ?: decimalFormatted
    }
}

fun Number.keepDecimals(decimalPlaces: Int): Number {
    if (decimalPlaces == 0) return toDouble().roundToInt()
    val factor = 10.0.pow(decimalPlaces)
    return (toDouble() * factor).roundToInt() / factor
}