package net.inceptioncloud.dragonfly.apps.modmanager.controls

import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.GraphicsEngine
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.contains
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseQuad
import net.inceptioncloud.dragonfly.engine.widgets.assembled.RoundedRectangle
import net.inceptioncloud.dragonfly.engine.widgets.primitive.FilledCircle
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.reflect.KMutableProperty0

class NumberControl(
    property: KMutableProperty0<out Number>,
    name: String,
    description: String? = null,
    val min: Double,
    val max: Double,
    val decimalPlaces: Int = 0,
    val transformer: (Number) -> Number = { it.keepDecimals(decimalPlaces) },
    val enableLiveUpdate: Boolean = false
) : OptionControlElement<Number>(property, name, description) {

    private val sliderWidth by lazy { controlWidth / 1.5 }
    private val sliderX by lazy { x + width - sliderWidth }
    private val sliderHeight = 6.0
    private val circleSize = 18.0

    private var dragging = false

    override fun controlAssemble(): Map<String, Widget<*>> = mapOf(
        "slider-background" to RoundedRectangle(),
        "slider-foreground" to FilledCircle()
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
    }

    private fun computeCircleX(): Double {
        val room = (max - min)
        val progress = (optionKey.get().toDouble().coerceIn(min..max) - min) / room
        return sliderX + (progress * sliderWidth) - (circleSize / 2)
    }

    override fun react(newValue: Number) {
        "slider-foreground"<FilledCircle> {
            if (dragging) {
                x = computeCircleX()
            } else {
                detachAnimation<MorphAnimation>()
                morph(20, EaseQuad.IN_OUT, FilledCircle::x to computeCircleX())?.start()
            }
        }
    }

    override fun update() {
        super.update()

        if (dragging) {
            if (enableLiveUpdate) {
                updateOptionKeyValue()
            } else {
                "slider-foreground"<FilledCircle> {
                    x = GraphicsEngine.getMouseX().coerceIn(sliderX..sliderX + sliderWidth) - circleSize / 2.0
                }
            }
        }
    }

    override fun handleMousePress(data: MouseData) {
        val c = getWidget<FilledCircle>("slider-foreground")!!
        val b = getWidget<RoundedRectangle>("slider-background")!!

        val mouseX = data.mouseX.toDouble()
        val mouseY = data.mouseY.toDouble()

        when {
            data in c -> dragging = true
            mouseX in b.x..b.x + b.width && mouseY in b.y - 20.0..b.y + b.height + 20.0 -> updateOptionKeyValue()
        }

        super.handleMousePress(data)
    }

    override fun handleMouseRelease(data: MouseData) {
        if (dragging) {
            updateOptionKeyValue()
            dragging = false
        }

        super.handleMouseRelease(data)
    }

    private fun updateOptionKeyValue() {
        val progress = (GraphicsEngine.getMouseX() - sliderX) / sliderWidth
        val value = (min + (progress * (max - min))).coerceIn(min..max)
        val transformed = transformer(value)
        if (transformed != optionKey.get())
            optionKey.set(transformed).also { println("-> $transformed") }
    }
}

fun Number.keepDecimals(decimalPlaces: Int): Number {
    if (decimalPlaces == 0) return toDouble().roundToInt()
    val factor = 10.0.pow(decimalPlaces)
    return (toDouble() * factor).roundToInt() / factor
}