package net.inceptioncloud.dragonfly.apps.modmanager.controls

import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.widgets.assembled.RoundedRectangle
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Circle
import net.inceptioncloud.dragonfly.engine.widgets.primitive.FilledCircle
import kotlin.reflect.KMutableProperty0

class NumberControl(
    property: KMutableProperty0<out Number>,
    name: String,
    description: String? = null,
    val min: Double,
    val max: Double,
    val decimalPlaces: Int = 0
) : OptionControlElement<Number>(property, name, description) {

    private val sliderWidth by lazy { controlWidth / 1.5 }
    private val sliderX by lazy { x + width - sliderWidth }
    private val sliderHeight = 6.0
    private val circleSize = 18.0

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
    }

    override fun handleMousePress(data: MouseData) {
    }
}