package net.inceptioncloud.dragonfly.options.entries

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.BluePalette
import net.inceptioncloud.dragonfly.options.OptionKey
import net.inceptioncloud.dragonfly.options.entries.util.ExternalApplier
import net.inceptioncloud.dragonfly.options.entries.util.OptionChoice
import net.inceptioncloud.dragonfly.transition.color.ColorTransition
import net.inceptioncloud.dragonfly.transition.number.SmoothDoubleTransition
import net.inceptioncloud.dragonfly.transition.supplier.ForwardBackward
import net.inceptioncloud.dragonfly.ui.renderer.RenderUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import java.awt.Color

/**
 * An option entry that can be used for predefined choices.
 *
 * Like other option entries, this one must specify a name, a description and a key.
 * In addition, there must be a set of available choices and an int value that represents
 * the default selected value.
 *
 * @param name the name of the option entry
 * @param description a simple description for the entry
 * @param default the identifier of the default value in the [choices]
 *
 * @property choices a set of choices from which one can be selected
 */
class OptionEntryMultipleChoice(
    name: String,
    description: String,
    val key: OptionKey<Int>,
    private val choices: List<OptionChoice>,
    default: Int,
    override var externalApplier: ((Int, OptionKey<Int>) -> Unit)? = null
) : OptionEntry<Int>(name, description), ExternalApplier<Int> {
    /**
     * Whether the left arrow is enabled.
     *
     * This value is updated when drawing the entry and evaluates to true if the mouse hovers
     * the arrow and if the currently selected choice isn't the last one. In this case, the arrow's
     * color will change from a dark blue to white.
     */
    private var enableLeftArrow: Boolean = false

    /**
     * Whether the right arrow is enabled.
     *
     * @see enableLeftArrow
     */
    private var enableRightArrow: Boolean = false

    /**
     * A simple color transition for changing the color of the left arrow.
     * The color is changed from a dark blue to white when the left arrow is enabled.
     *
     * @see enableLeftArrow
     */
    private val leftArrowHoverColor = ColorTransition.builder()
        .start(Color(0x355571)).end(BluePalette.FOREGROUND)
        .autoTransformator(ForwardBackward { enableLeftArrow })
        .amountOfSteps(15).build()

    /**
     * A simple color transition for changing the color of the right arrow.
     * The color is changed from a dark blue to white when the right arrow is enabled.
     *
     * @see enableRightArrow
     */
    private val rightArrowHoverColor = ColorTransition.builder()
        .start(Color(0x355571)).end(BluePalette.FOREGROUND)
        .autoTransformator(ForwardBackward { enableRightArrow })
        .amountOfSteps(15).build()

    /**
     * The option choice that is currently selected by the user.
     *
     * This value can change during the lifetime of the entry. The default value is set
     * to the choice with the default id from the constructor.
     *
     * @see OptionChoice
     * @see getChoiceByIdentifier
     */
    private var selectedChoice = getChoiceByIdentifier(key.get())!!

    /**
     * The index of the currently selected option.
     *
     * Note that this isn't the identifier of the option choice, but the index of the selected
     * option choice in the choice list.
     *
     * @see OptionChoice.identifier
     * @see choices
     */
    private var selectedIndex = choices.indexOf(selectedChoice)

    /**
     * Extension to [drawEntry].
     */
    override fun drawContent(x: Int, y: Int, height: Int, width: Int) {
        val viewWidth = 80
        val viewHeight = height - 6
        val viewX = x + width - viewWidth - 6
        val viewY = y + 3

        val fontRenderer = Dragonfly.fontManager.regular
        fontRenderer.drawCenteredString(
            selectedChoice.displayString,
            viewX + viewWidth / 2, viewY + height / 2 - fontRenderer.height / 2,
            BluePalette.PRIMARY.rgb, false
        )

        val viewXD = viewX.toDouble()
        val viewCenterVertical = (viewY + viewHeight / 2).toDouble()
        val arrowTop = (viewY + viewHeight / 2 - 3).toDouble()
        val arrowBottom = (viewY + viewHeight / 2 + 3).toDouble()

        // update the state of the left arrow
        enableLeftArrow = Gui.getMouseX() in viewX - 3..viewX + viewWidth - 9
                && Gui.getMouseY() in arrowTop..arrowBottom
                && previous() != null

        // update the state of the right arrow
        enableRightArrow = Gui.getMouseX() in viewX + 9..viewX + viewWidth + 3
                && Gui.getMouseY() in arrowTop..arrowBottom
                && next() != null

        // Arrow Left
        val leftColor = leftArrowHoverColor.get()
        GlStateManager.color(leftColor.red / 255F, leftColor.green / 255F, leftColor.blue / 255F, 1.0F)
        RenderUtils.drawLine(viewXD, viewCenterVertical, (viewX + 3).toDouble(), arrowTop, 3F)
        RenderUtils.drawLine(viewXD, viewCenterVertical, (viewX + 3).toDouble(), arrowBottom, 3F)

        // Arrow Right
        val rightColor = rightArrowHoverColor.get()
        GlStateManager.color(rightColor.red / 255F, rightColor.green / 255F, rightColor.blue / 255F, 1.0F)
        RenderUtils.drawLine(
            (viewX + viewWidth).toDouble(), viewCenterVertical,
            (viewX + viewWidth - 3).toDouble(), arrowTop,
            3F
        )
        RenderUtils.drawLine(
            (viewX + viewWidth).toDouble(), viewCenterVertical,
            (viewX + viewWidth - 3).toDouble(), arrowBottom,
            3F
        )

        if (externalApplier != null) {
            valueChanged = renderChangeState(x, y, height, width, key, valueCache)
            textOffset = ((height - 7) * transitionExternalApplier.get()).toInt()
        }
    }

    /**
     * Called when the entry is (double-) clicked.
     */
    override fun clicked(isDoubleClick: Boolean, mouseOnEntryX: Int, mouseOnEntryY: Int, entryWidth: Int, entryHeight: Int) {
        val viewWidth = 80
        val viewHeight = entryHeight - 6
        val viewX = entryWidth - viewWidth - 6
        val viewY = 3
        val arrowTop = viewY + viewHeight / 2 - 3
        val arrowBottom = viewY + viewHeight / 2 + 3

        if (mouseOnEntryY in arrowTop..arrowBottom) {
            if (mouseOnEntryX in viewX - 3..viewX + 9 && enableLeftArrow) {
                selectedIndex--
            } else if (mouseOnEntryX in viewX + viewWidth - 9..viewX + viewWidth + 3 && enableRightArrow) {
                selectedIndex++
            } else return

            Minecraft.getMinecraft().soundHandler.playSound(
                PositionedSoundRecord.create(ResourceLocation("gui.button.press"), 1.0F)
            )

            selectedChoice = choices[selectedIndex]
            if (externalApplier == null) {
                key.set(selectedChoice.identifier)
            } else {
                valueCache = selectedChoice.identifier
            }
        }
    }

    /**
     * Retrieves an [OptionChoice] by its identifier.
     *
     * Since every option should have an unique int identifier, this method should always return
     * a specific choice.
     *
     * @see OptionChoice.identifier
     */
    private fun getChoiceByIdentifier(id: Int): OptionChoice? {
        return choices.find { it.identifier == id }
    }

    /**
     * A quick function to retrieve the next choice.
     * @return `null` if the selected choice is the last one, otherwise the next choice
     */
    private fun next(): OptionChoice? {
        return if (choices.size > selectedIndex + 1) choices[selectedIndex + 1] else null
    }

    /**
     * A quick function to retrieve the previous choice.
     * @return `null` if the selected choice is the first one, otherwise the previous choice
     */
    private fun previous(): OptionChoice? {
        return if (selectedIndex - 1 >= 0) choices[selectedIndex - 1] else null
    }

    /**
     * Convenient function to access the value of the option entry.
     */
    operator fun invoke(): Int? = key.get()

    /**
     * A cache for the current value in case it should be applied externally.
     */
    override var valueCache: Int = selectedChoice.identifier

    /**
     * Whether the current value isn't the value of the key.
     */
    override var valueChanged: Boolean = false

    /**
     * A transition that fades in the text offset and save icon.
     */
    override var transitionExternalApplier: SmoothDoubleTransition = SmoothDoubleTransition.builder()
        .start(0.0).end(1.0).fadeIn(15).stay(10).fadeOut(15)
        .autoTransformator(ForwardBackward { valueChanged })
        .build()

    /**
     * The method that applies the value using the [externalApplier].
     *
     * It is called when the "Save and Exit" button is pressed thus the value should be
     * applied.
     */
    override fun applyExternally() {
        if (valueCache != key.get())
            externalApplier?.invoke(valueCache, key)
    }
}
