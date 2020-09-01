package net.inceptioncloud.dragonfly.options.entries

import net.inceptioncloud.dragonfly.options.OptionKey
import net.inceptioncloud.dragonfly.options.entries.util.ExternalApplier
import net.inceptioncloud.dragonfly.transition.number.SmoothDoubleTransition
import net.inceptioncloud.dragonfly.transition.supplier.ForwardBackward
import net.inceptioncloud.dragonfly.ui.components.toggle.UISwitch

/**
 * A specific option entry for boolean values.
 */
class OptionEntryBoolean(
    name: String,
    description: String,
    private val typeKey: OptionKey<Boolean>,
    override var externalApplier: ((Boolean, OptionKey<Boolean>) -> Unit)? = null
) : OptionEntry<Boolean>(name, description), ExternalApplier<Boolean> {
    /**
     * Width of the switch.
     */
    private val switchWidth = 16

    /**
     * Height of the switch.
     */
    private val switchHeight = 8

    /**
     * A cache for the current value in case it should be applied externally.
     */
    override var valueCache: Boolean = typeKey.get()

    /**
     * The UI switch element that is responsible for displaying and changing the value of the [OptionKey].
     */
    private val switch: UISwitch = UISwitch(x, y, switchWidth, switchHeight,
        {
            if (externalApplier == null)
                typeKey.get()
            else
                valueCache
        },
        { value ->
            if (externalApplier == null)
                typeKey.set(value)
            else
                valueCache = value
        })

    /**
     * Called when the entry is drawn at a certain position with the given height and with.
     */
    override fun drawContent(x: Int, y: Int, height: Int, width: Int) {
        switch.x = x + width - 5 - switch.width
        switch.y = y + height / 2 - switch.height / 2

        switch.draw()

        if (externalApplier != null) {
            valueChanged = renderChangeState(x, y, height, width, typeKey, valueCache)
            textOffset = ((height - 7) * transitionExternalApplier.get()).toInt()
        }
    }

    /**
     * Called when the entry is (double-) clicked.
     */
    override fun clicked(isDoubleClick: Boolean, mouseOnEntryX: Int, mouseOnEntryY: Int, entryWidth: Int,
                         entryHeight: Int) {
        switch.mouseClicked(mouseOnEntryX + x, mouseOnEntryY + y)
    }

    /**
     * Function to be overridden in order to return the key.
     */
    fun getKey(): OptionKey<Boolean> {
        return typeKey
    }

    /**
     * Convenient function to access the value of the option entry.
     */
    operator fun invoke(): Boolean? = typeKey.get()

    /**
     * Whether the current value isn't the value of the key.
     */
    override var valueChanged: Boolean = false

    /**
     * The method that applies the value using the [externalApplier].
     *
     * It is called when the "Save and Exit" button is pressed thus the value should be
     * applied.
     */
    override fun applyExternally() {
        if (valueCache != typeKey.get())
            externalApplier?.invoke(valueCache, typeKey)
    }

    /**
     * A transition that fades in the text offset and save icon.
     */
    override var transitionExternalApplier: SmoothDoubleTransition = SmoothDoubleTransition.builder()
        .start(0.0).end(1.0)
        .fadeIn(15).stay(10).fadeOut(15)
        .autoTransformator(ForwardBackward { valueChanged })
        .build()
}