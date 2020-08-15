package net.inceptioncloud.dragonfly.options.entries

import net.inceptioncloud.dragonfly.options.OptionKey
import net.inceptioncloud.dragonfly.ui.components.slider.UISlider
import kotlin.math.roundToInt

/**
 * A modification of the [OptionEntryRangeDouble] that rounds the value to an integer.
 *
 * @property name the name of the option that is displayed on the left
 * @property description a text that describes that the option does
 * @property typeKey the key that is associated with the option
 */
class OptionEntryRangeInt(name: String, description: String, private val typeKey: OptionKey<Int>,
                          minValue: Int, maxValue: Int,
                          formatterIn: ((Double) -> String)?)
    : OptionEntry<Int>(name, description) {
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
    init {
        formatter = formatterIn ?: { value ->
            val round = value.roundToInt()
            round.toString()
        }
    }

    /**
     * A UI Slider element that is located on the right of the option entry and is used to change and display the value.
     *
     * The min- and max-values from the constructor are passed in here and the value of the [typeKey] is automatically
     * updated on change. A formatter is used that rounds the value on two decimal digits and displays it raw next to
     * the slider.
     */
    private val slider: UISlider = UISlider(0, 0, 0, minValue.toDouble(), maxValue.toDouble(),
        typeKey.get().toDouble(), { value -> typeKey.set(value.roundToInt()) }, formatter)

    /**
     * Called when the entry is drawn at a certain position with the given height and with.
     */
    override fun drawContent(x: Int, y: Int, height: Int, width: Int) {
        slider.width = 61
        slider.x = x + width - 7 - slider.width
        slider.y = y + height / 2

        slider.draw()
    }

    /**
     * Called when the entry is (double-) clicked.
     */
    override fun clicked(isDoubleClick: Boolean, mouseOnEntryX: Int, mouseOnEntryY: Int, entryWidth: Int, entryHeight: Int) {
    }

    /**
     * Fires the [UISlider.mousePressed] function when the left mouse button is pressed.
     *
     * The slider uses this information to update the slider position on click or switch into the mouse dragging state.
     *
     * @see UISlider.isMouseDown
     * @see UISlider.sliderPosition
     */
    override fun mousePressed(mouseX: Int, mouseY: Int, eventButton: Int) {
        if (eventButton == 0) {
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
    override fun mouseReleased(mouseX: Int, mouseY: Int, eventButton: Int) {
        if (eventButton == 0) {
            slider.mouseReleased(mouseX, mouseY)
        }
    }

    /**
     * Called by the list when a key is pressed.
     */
    override fun keyTyped(typedChar: Char, keyCode: Int) {
        slider.keyTyped(typedChar, keyCode)
    }

    /**
     * Function to be overridden in order to return the key.
     */
    fun getKey(): OptionKey<Int> {
        return typeKey
    }

    /**
     * Convenient function to access the value of the option entry.
     */
    operator fun invoke(): Int? = typeKey.get()
}