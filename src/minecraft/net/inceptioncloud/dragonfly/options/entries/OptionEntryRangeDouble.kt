package net.inceptioncloud.dragonfly.options.entries

import net.inceptioncloud.dragonfly.options.OptionKey
import net.inceptioncloud.dragonfly.options.entries.util.ExternalApplier
import net.inceptioncloud.dragonfly.transition.number.SmoothDoubleTransition
import net.inceptioncloud.dragonfly.transition.supplier.ForwardBackward
import net.inceptioncloud.dragonfly.ui.components.slider.UISlider
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.math.roundToInt

/**
 * A specific entry for the option ui list that can select a double value in a given range.
 *
 * @property name the name of the option that is displayed on the left
 * @property description a text that describes that the option does
 * @property typeKey the key that is associated with the option
 */
class OptionEntryRangeDouble(
        name: String,
        description: String,
        private val typeKey: OptionKey<Double>,
        minValue: Double,
        maxValue: Double,
        formatterIn: ((Double) -> String)?,
        override var externalApplier: ((Double, OptionKey<Double>) -> Unit)? = null
) : OptionEntry<Double>(name, description), ExternalApplier<Double>
{
    /**
     * The non-nullable version of the formatter constructor parameter.
     *
     * This variable is set in the init block.
     */
    private var formatter: (Double) -> String

    /**
     * Checks the value of the formatter that was passed into the constructor and makes it non-nullable.
     *
     * If the input formatter is null, a default one will be used.
     */
    init
    {
        formatter = formatterIn ?: { value ->
            val round = (value * 100).roundToInt() / 100.0
            val nf: NumberFormat = NumberFormat.getNumberInstance(Locale.US)
            val df: DecimalFormat = nf as DecimalFormat

            df.applyPattern("0.00")
            df.format(round)
        }
    }

    /**
     * A UI Slider element that is located on the right of the option entry and is used to change and display the value.
     *
     * The min- and max-values from the constructor are passed in here and the value of the [typeKey] is automatically
     * updated on change. A formatter is used that rounds the value on two decimal digits and displays it raw next to
     * the slider.
     */
    private val slider: UISlider = UISlider(0, 0, 0, minValue, maxValue, typeKey.get(),
            { value ->
                if (externalApplier == null)
                    typeKey.set(value)
                else
                    valueCache = value
            }, formatter)

    /**
     * Called when the entry is drawn at a certain position with the given height and with.
     */
    override fun drawContent(x: Int, y: Int, height: Int, width: Int)
    {
        slider.width = 61
        slider.x = x + width - 7 - slider.width
        slider.y = y + height / 2

        slider.draw()

        if (externalApplier != null)
        {
            valueChanged = renderChangeState(x, y, height, width, typeKey, valueCache)
            textOffset = ((height - 7) * transitionExternalApplier.get()).toInt()
        }
    }

    /**
     * Called when the entry is (double-) clicked.
     */
    override fun clicked(isDoubleClick: Boolean, mouseOnEntryX: Int, mouseOnEntryY: Int, entryWidth: Int, entryHeight: Int)
    {
    }

    /**
     * Fires the [UISlider.mousePressed] function when the left mouse button is pressed.
     *
     * The slider uses this information to update the slider position on click or switch into the mouse dragging state.
     *
     * @see UISlider.isMouseDown
     * @see UISlider.sliderPosition
     */
    override fun mousePressed(mouseX: Int, mouseY: Int, eventButton: Int)
    {
        if (eventButton == 0)
        {
            slider.mousePressed(mouseX, mouseY)
        }
    }

    /**
     * Fires the [UISlider.mouseReleased] function when the left mouse button is dragged.
     *
     * Whenever the mouse is released, the slider switches out of the mouse dragging state and calls the change responder.
     * This is the case both when the mouse is clicked (pressed and released) and dragged.
     *
     * @see UISlider.isMouseDown
     * @see UISlider.changeResponder
     */
    override fun mouseReleased(mouseX: Int, mouseY: Int, eventButton: Int)
    {
        if (eventButton == 0)
        {
            slider.mouseReleased(mouseX, mouseY)
        }
    }

    /**
     * Called by the list when a key is pressed.
     */
    override fun keyTyped(typedChar: Char, keyCode: Int)
    {
        slider.keyTyped(typedChar, keyCode)
    }

    /**
     * Function to be overridden in order to return the key.
     */
    fun getKey(): OptionKey<Double>
    {
        return typeKey
    }

    /**
     * The method that applies the value using the [externalApplier].
     *
     * It is called when the "Save and Exit" button is pressed thus the value should be
     * applied.
     */
    override fun applyExternally()
    {
        if (valueCache != typeKey.get())
            externalApplier?.invoke(valueCache, typeKey)
    }

    /**
     * A cache for the current value in case it should be applied externally.
     */
    override var valueCache: Double = typeKey.get()

    /**
     * Whether the current value isn't the value of the key.
     */
    override var valueChanged: Boolean = false

    /**
     * A transition that fades in the text offset and save icon.
     */
    override var transitionExternalApplier: SmoothDoubleTransition = SmoothDoubleTransition.builder()
        .start(0.0).end(1.0)
        .fadeIn(15).stay(10).fadeOut(15)
        .autoTransformator(ForwardBackward { valueChanged })
        .build()
}