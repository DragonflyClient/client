package net.inceptioncloud.dragonfly.ui.components.toggle

import net.inceptioncloud.dragonfly.design.color.BluePalette
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.transition.color.ColorTransition
import net.inceptioncloud.dragonfly.transition.number.SmoothDoubleTransition
import net.inceptioncloud.dragonfly.transition.supplier.ForwardBackward
import net.inceptioncloud.dragonfly.ui.renderer.RenderUtils
import net.minecraft.client.renderer.GlStateManager
import java.awt.Color
import java.util.function.BooleanSupplier
import java.util.function.Consumer

/**
 * A simple, modern designed switch that can be implemented in any UI.
 *
 * It uses a [BooleanSupplier] to receive and a
 * [Consumer] to set the value what makes it compatible with any boolean value and the builtin option system.
 *
 * @property x the x-coordinate of the bottom left corner of the switch
 * @property y the y-coordinate of the bottom left corner of the switch
 * @property width the width of the switch
 * @property height the height of the height
 * @property valueGetter a function that is invoked to receive the boolean value
 * @property valueSetter a function that is invoked to change the boolean value
 *
 * @see net.inceptioncloud.dragonfly.options.OptionKey
 */
class UISwitch(var x: Int, var y: Int, var width: Int, var height: Int,
               private val valueGetter: () -> Boolean, private val valueSetter: (Boolean) -> Unit) {
    /**
     * The boolean value that was active when the switch was created.
     *
     * It's purpose is to check if the switch was initialized with the value `false` so the first value change is not
     * animated.
     */
    private val initialValue = valueGetter.invoke()

    /**
     * This property is initialized with the value `true`, if the [initialValue] is `false`, what means that the
     * first value change transition should not be displayed (but runs in the background). When the transition has
     * finished, this value changes to `false` since the following transitions should no longer be ignored.
     */
    private var ignoreTransition = initialValue

    /**
     * Moves the left bound of the inner switch circle.
     *
     * In order to provide a smooth transition when switching from **on to off**, this transition starts when the
     * [transitionRight] has finished. When switching from **off to on**, this transition starts first.
     */
    private val transitionLeft: SmoothDoubleTransition = SmoothDoubleTransition.builder()
        .start(0.0).end(1.0)
        .fadeIn(10).stay(10).stay(10)
        .autoTransformator {
            if (valueGetter.invoke() && transitionRight.isAtEnd)
                return@autoTransformator 1
            else if (!valueGetter.invoke())
                return@autoTransformator -1
            else
                return@autoTransformator 0
        }
        .reachEnd { ignoreTransition = false }
        .build()

    /**
     * Moves the right bound of the inner switch circle.
     *
     * In order to provide a smooth transition when switching from **off to on**, this transition starts when the
     * [transitionRight] has finished. When switching from **on to off**, this transition starts first.
     */
    private val transitionRight: SmoothDoubleTransition = SmoothDoubleTransition.builder()
        .start(0.0).end(1.0)
        .fadeIn(10).stay(10).stay(10)
        .autoTransformator {
            if (valueGetter.invoke())
                return@autoTransformator 1
            else if (!valueGetter.invoke() && transitionLeft.isAtStart)
                return@autoTransformator -1
            else
                return@autoTransformator 0
        }.build()

    /**
     * Provides the transition between **green** (value is `true`) and **red** (value is `false`).
     */
    private val transitionColor = ColorTransition.builder()
        .start(DragonflyPalette.accentBright.base).end(DragonflyPalette.accentDark.base)
        .amountOfSteps(50)
        .autoTransformator(ForwardBackward { !valueGetter.invoke() }).build()

    /**
     * Draws the switch into the current UI.
     */
    fun draw() {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F)

        val valueLeft = if (ignoreTransition) 1.0 else transitionLeft.get()
        val valueRight = if (ignoreTransition) 1.0 else transitionRight.get()

        val innerSize = 6
        val innerOffset = width - 2 - innerSize
        val innerLeft = (x + 1 + (innerOffset * valueLeft)).toInt()
        val innerRight = (x + 1 + innerSize + ((width - innerSize - 2) * valueRight)).toInt()

        RenderUtils.drawRoundRect(x, y, width, height, height, BluePalette.FOREGROUND)
        RenderUtils.drawRoundRect(
            innerLeft, y + height / 2 - innerSize / 2,
            innerRight - innerLeft, innerSize,
            10, transitionColor.get()
        )
    }

    /**
     * Called on every registered mouse input. This method checks if the input is valid (in the switch bounds)
     * and then changes the value using the [valueSetter] to the opposite of the current value of the [valueGetter].
     */
    fun mouseClicked(mouseX: Int, mouseY: Int) {
        if (mouseX in x..(x + width) && mouseY in y..(y + height))
            valueSetter.invoke(!valueGetter.invoke())
    }
}