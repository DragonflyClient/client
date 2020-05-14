package net.inceptioncloud.minecraftmod.ui.components.slider

import net.inceptioncloud.minecraftmod.Dragonfly
import net.inceptioncloud.minecraftmod.design.color.BluePalette
import net.inceptioncloud.minecraftmod.utils.RenderUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import kotlin.math.pow

/**
 * A simple, modern designed slider for double ranges.
 *
 * It uses a [changeResponder] that is fired with the value of
 * the slider each time the mouse was released. The value can be changed by moving the circle on the slider or simply
 * clicking on a slider position.
 *
 * @property x the x-coordinate of the left side of the slider
 * @property y the y-coordinate of the slider line
 * @property width the width of the slider
 * @property min the smallest value that the slider can have (left bound)
 * @property max the largest value that the slider can have (right bound)
 * @property default the value that the slider has when it's being initialized
 * @property changeResponder a function that is fired when the mouse was released (this does not mean that the value
 * has changed)
 * @property formatter a function that is invoked with the current value and builds a string from that value that is
 * displayed on the left of the slider
 */
class UISlider(var x: Int, var y: Int, var width: Int,
               private val min: Double, private val max: Double, private val default: Double,
               private val changeResponder: (Double) -> Unit, private val formatter: (Double) -> String)
{
    /**
     * The current position of the slider, **not the current value**!
     *
     * It is a floating point value from 0.0 to 1.0 that is used represents the relative position of the slider. This
     * value is used to calculate the slider value in [getSliderValue] and display the slider position in [draw]
     *
     * @see getSliderValue
     */
    private var sliderPosition = (default - min) / (max - min)

    /**
     * Whether the mouse is currently down on the slider.
     *
     * If this value is true, the slider reacts to mouse movements
     * and modifies the [sliderPosition] accordingly. When pressing the mouse, the value is set to `true` and when
     * releasing the mouse, it changes to `false`.
     */
    private var isMouseDown = false

    /**
     * Whether the slider is currently focused because the last mouse click was on it.
     *
     * If this value is true, the slider's value can be modified using the arrow keys.
     * When clicking on the slider, the value is set to true. When clicking anywhere else on the screen
     * (not on the slider) the value is set to false.
     *
     * @see mousePressed
     */
    private var focused = false

    /**
     * The string that is displayed on the left of the slider.
     *
     * Each time the value changes, [formatDisplayString] is called to update the display string. After that, the
     * result is stored in this value so it doesn't have to be calculated while drawing.
     *
     * @see formatDisplayString
     */
    var displayString: String? = formatDisplayString()

    /**
     * Calculates the value that the current [sliderPosition] is equal to.
     *
     * This represents the **real value** that is between the [minimal value][min] and the [maximal value][max]. The
     * value is based on the left and right bounds and the relative [sliderPosition].
     *
     * @see min
     * @see max
     * @see sliderPosition
     */
    private fun getSliderValue(): Double = this.min + (this.max - this.min) * sliderPosition

    /**
     * Creates the text that is displayed on the left of the slider.
     *
     * Invokes the [formatter] with the current slider value in order to generate a string. The formatter can be
     * customized in the constructor so there are plenty of possibilities to display the current value.
     */
    private fun formatDisplayString(): String = formatter.invoke(getSliderValue())

    /**
     * Draws the slider and all its components on the screen.
     *
     * This method must be called from the parent object that is holding the slider. The [x], [y] and [width]
     * values can be modified as desired. Before the slider is drawn, the slider position is updated according to the
     * mouse location of the slider is currently in dragging state. This helps to provide a smooth dragging.
     *
     * @see isMouseDown
     */
    fun draw()
    {
        val mouseX = Mouse.getX() / ScaledResolution(Minecraft.getMinecraft()).scaleFactor

        if (isMouseDown)
        {
            sliderPosition = (mouseX - x) / width.toDouble()

            if (sliderPosition < 0.0)
            {
                sliderPosition = 0.0
            } else if (sliderPosition > 1.0)
            {
                sliderPosition = 1.0
            }

            displayString = formatDisplayString()
        }

        val f = 2.0
        GlStateManager.scale(1 / f, 1 / f, 1 / f)
        RenderUtils.drawRoundRect((x * f).toInt(), (y * f - 1).toInt(), (width * f).toInt(), 2, 0, BluePalette.FOREGROUND)
        RenderUtils.drawFilledCircle(((x + (width * sliderPosition)) * f).toInt(), (y * f).toInt(), 4F, BluePalette.PRIMARY_LIGHT)

        val fontRenderer = Dragonfly.fontDesign.retrieveOrBuild("", 34)
        val stringWidth = fontRenderer.getStringWidth(displayString)

        fontRenderer.drawString(displayString, ((x - 5) * f - stringWidth).toInt(), ((y - 3.5) * f).toInt(), BluePalette.PRIMARY_LIGHT.rgb)

        GlStateManager.scale(f, f, f)
    }

    /**
     * Updates the slider position and dragging state when the mouse is clicking on the slider.
     *
     * This method must be called from the parent object that is holding the slider. The check whether the mouse is
     * on the slider is performed in the method.
     *
     * @see sliderPosition
     * @see isMouseDown
     */
    fun mousePressed(mouseX: Int, mouseY: Int)
    {
        if (mouseX in (x - 3)..(x + width + 3) && mouseY in (y - 4)..(y + 4))
        {
            sliderPosition = (mouseX - x) / width.toDouble()

            if (sliderPosition < 0.0)
            {
                sliderPosition = 0.0
            } else if (sliderPosition > 1.0)
            {
                sliderPosition = 1.0
            }

            displayString = formatDisplayString()
            isMouseDown = true
            focused = true
        } else
        {
            focused = false
        }
    }

    /**
     * Resets the dragging state and fires the responder when releasing the mouse.
     *
     * This method must be called from the parent object that is holding the slider.
     *
     * @see isMouseDown
     * @see changeResponder
     */
    fun mouseReleased(mouseX: Int, mouseY: Int)
    {
        if (isMouseDown)
        {
            isMouseDown = false
            changeResponder.invoke(getSliderValue())
        }
    }

    /**
     * Updates the value of the slider based on the step accuracy when it is focused.
     *
     * This method must be called from the parent object that is holding the slider.
     * When the arrow-left or arrow-right key is pressed, the value will be updated.
     */
    fun keyTyped(typedChar: Char, keyCode: Int)
    {
        if (focused)
        {
            val newValue: Double = when (keyCode)
            {
                Keyboard.KEY_RIGHT ->
                {
                    getSliderValue() + getStepAccuracy()
                }
                Keyboard.KEY_LEFT ->
                {
                    getSliderValue() - getStepAccuracy()
                }
                else -> return
            }.coerceAtLeast(min).coerceAtMost(max)

            sliderPosition = (newValue - min) / (max - min)
            displayString = formatDisplayString()
            changeResponder.invoke(getSliderValue())
        }
    }

    /**
     * Calculates the accuracy of the slider by reading the formatted display string.
     *
     * According to this amount, the value that should be added/removed when using the arrow
     * keys to control to slider is calculated.
     */
    private fun getStepAccuracy(): Double
    {
        val string = formatDisplayString()

        return if (string.endsWith("%") || !string.contains("."))
        {
            1.0
        } else
        {
            try
            {
                val decimalString = string.split(".")[1]
                val decimalDigits = decimalString.length
                1 / 10.0.pow(decimalDigits.toDouble())
            }
            catch (e: NumberFormatException)
            {
                1.0
            }
        }
    }

    override fun equals(other: Any?): Boolean
    {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UISlider

        if (x != other.x) return false
        if (y != other.y) return false
        if (width != other.width) return false
        if (min != other.min) return false
        if (max != other.max) return false
        if (default != other.default) return false
        if (changeResponder != other.changeResponder) return false
        if (formatter != other.formatter) return false
        if (sliderPosition != other.sliderPosition) return false
        if (isMouseDown != other.isMouseDown) return false
        if (focused != other.focused) return false
        if (displayString != other.displayString) return false

        return true
    }

    override fun hashCode(): Int
    {
        var result = x
        result = 31 * result + y
        result = 31 * result + width
        result = 31 * result + min.hashCode()
        result = 31 * result + max.hashCode()
        result = 31 * result + default.hashCode()
        result = 31 * result + changeResponder.hashCode()
        result = 31 * result + formatter.hashCode()
        result = 31 * result + sliderPosition.hashCode()
        result = 31 * result + isMouseDown.hashCode()
        result = 31 * result + focused.hashCode()
        result = 31 * result + (displayString?.hashCode() ?: 0)
        return result
    }
}
