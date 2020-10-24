package net.inceptioncloud.dragonfly.controls

import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.contains
import net.inceptioncloud.dragonfly.engine.internal.MouseData
import net.inceptioncloud.dragonfly.engine.internal.Widget
import net.inceptioncloud.dragonfly.engine.internal.WidgetColor
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseQuint
import net.inceptioncloud.dragonfly.engine.widgets.assembled.RoundedRectangle
import net.inceptioncloud.dragonfly.options.OptionKey
import net.inceptioncloud.dragonfly.utils.Either
import kotlin.reflect.KMutableProperty0

class BooleanControl(
    either: Either<KMutableProperty0<out Boolean>, OptionKey<Boolean>>,
    name: String,
    description: String? = null,
    var changeValue: (() -> Boolean)? = { true }
) : OptionControlElement<Boolean>(either, name, description) {

    val switchWidth = 50.0f
    val switchHeight = 24.0f
    val innerSize = 18.0f
    val innerMargin = (switchHeight - innerSize) / 2

    override fun controlAssemble(): Map<String, Widget<*>> = mapOf(
        "switch-background" to RoundedRectangle(),
        "switch-foreground" to RoundedRectangle()
    )

    override fun controlUpdateStructure() {

        val background = "switch-background"<RoundedRectangle> {
            width = switchWidth
            height = switchHeight
            x = controlX + controlWidth - width
            y = this@BooleanControl.y + (this@BooleanControl.height - height) / 2
            arc = height / 2
            color = computeColor()
        }!!

        "switch-foreground"<RoundedRectangle> {
            width = innerSize
            height = innerSize
            x = computeInnerX(background.x, background.width)
            y = this@BooleanControl.y + (this@BooleanControl.height - height) / 2
            arc = height / 2
            color = WidgetColor(255, 255, 255)
        }
    }

    override fun react(newValue: Boolean) {
        val background = getWidget<RoundedRectangle>("switch-background")!!
        val foreground = getWidget<RoundedRectangle>("switch-foreground")!!

        background.morph(40, EaseQuint.IN_OUT, RoundedRectangle::color to computeColor())?.start()
        foreground.morph(40, EaseQuint.IN_OUT, RoundedRectangle::x to computeInnerX(background.x, background.width))
            ?.start()
    }

    override fun handleMousePress(data: MouseData) {
        if (data in getWidget<RoundedRectangle>("switch-background")) {
            if (changeValue != null && changeValue!!.invoke()) {
                optionKey.set(!optionKey.get())
            }
        }
        super.handleMousePress(data)
    }

    private fun computeColor() =
        if (optionKey.get()) DragonflyPalette.accentNormal else WidgetColor(230, 230, 230)

    private fun computeInnerX(backgroundX: Float, backgroundWidth: Float) =
        if (optionKey.get()) backgroundX + backgroundWidth - innerMargin - innerSize else backgroundX + innerMargin
}