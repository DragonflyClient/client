package net.inceptioncloud.minecraftmod.options.entries

import net.inceptioncloud.minecraftmod.InceptionMod
import net.inceptioncloud.minecraftmod.design.color.BluePalette.BACKGROUND
import net.inceptioncloud.minecraftmod.design.color.BluePalette.FOREGROUND
import net.inceptioncloud.minecraftmod.design.color.RGB
import net.inceptioncloud.minecraftmod.transition.number.SmoothDoubleTransition
import net.inceptioncloud.minecraftmod.transition.supplier.ForwardBackward
import net.inceptioncloud.minecraftmod.ui.components.list.UIListEntry
import net.minecraft.client.gui.Gui.*
import org.lwjgl.input.Mouse

/**
 * An abstract entry that can be used for every type of option key.
 */
abstract class OptionEntry<T>(val name: String, val description: String) : UIListEntry()
{
    /**
     * The time when the cursor moved on the trigger.
     *
     * If the cursor is no longer on the trigger, this value is set to `0L`. It is used
     * in [isDescriptionShown] to track the hover time and animate the description in.
     */
    private var triggerHoverMoment: Long = 0

    /**
     * A local cache of the last height value that was used in the [drawEntry] function.
     *
     * Since [isTriggerHovered] needs the height value, it is stored here every time the
     * entry is drawn.
     */
    private var height = 0

    /**
     * The offset for the name of the entry that is rendered by default. This value is
     * changed if a child entry needs to display an icon next to the name.
     */
    protected var textOffset = 0

    /**
     * The transition that flies in the description and its background.
     *
     * When the condition ([isDescriptionShown]) is fulfilled, this transition will start
     * moving a background and the description text from the left over the entry.
     */
    private val transitionDescription = SmoothDoubleTransition.builder()
        .start(0.0).end(1.0)
        .fadeIn(20).stay(20).fadeOut(0)
        .autoTransformator(ForwardBackward { isDescriptionShown() })
        .build()

    /**
     * The transition that shows the hover hint when the cursor is moved over the trigger.
     */
    private val transitionHoverHint = SmoothDoubleTransition.builder()
        .start(0.0).end(1.0)
        .fadeIn(20).stay(20).fadeOut(20)
        .autoTransformator(ForwardBackward { isTriggerHovered() })
        .build()

    /**
     * Called when the entry is drawn at a certain position with the given height and with.
     */
    final override fun drawEntry(x: Int, y: Int, height: Int, width: Int)
    {
        this.height = height

        drawRect(x, y, x + width, y + height, RGB.of(BACKGROUND).alpha(0.7F).rgb())
        InceptionMod.getInstance().fontDesign.regular.drawString(name, x + 5 + textOffset, y + 7, FOREGROUND.rgb)

        drawContent(x, y, height, width)

        // Manages the hover state of the description trigger
        if (isTriggerHovered())
        {
            if (triggerHoverMoment == 0L)
            {
                triggerHoverMoment = System.currentTimeMillis()
            } else if (isDescriptionShown() && !Mouse.isGrabbed())
            {
                Mouse.setGrabbed(true)
            }
        } else if (triggerHoverMoment != 0L)
        {
            triggerHoverMoment = 0
            Mouse.setGrabbed(false)
        }

        // Draws the description over the content
        drawRect(x, y, x + 1, y + height, RGB.of(FOREGROUND).alpha(transitionHoverHint.get().toFloat() / 2).rgb())
        drawRect(x, y, x + (width * transitionDescription.get()).toInt(), y + height, BACKGROUND.rgb)

        val fontRenderer = InceptionMod.getInstance().fontDesign.retrieveOrBuild("", 12)
        val lines = fontRenderer.listFormattedStringToWidth(description, width * 2)
        val centerY = y + height / 2 - 1
        var fontY = centerY - (lines.size * fontRenderer.height / 2)

        for (string in lines)
        {
            val stringWidth = fontRenderer.getStringWidth(string)
            fontRenderer.drawString(string, x + 5 - ((stringWidth + 10) * (1 - transitionDescription.get())).toInt(),
                    fontY + (fontRenderer.height / 2), FOREGROUND.rgb)

            fontY += fontRenderer.height
        }
    }

    /**
     * Checks whether the cursor stayed on the trigger for at least two seconds.
     *
     * If the condition is fulfilled, the description appears from the left since the
     * [transitionDescription] transition is running.
     */
    private fun isDescriptionShown() = triggerHoverMoment != 0L && System.currentTimeMillis() - triggerHoverMoment >= 1_200

    /**
     * Checks whether the description trigger on the left side of the entry is hovered.
     *
     * If it's hovered, a white line appears that indicates that. After the cursor stays on
     * this trigger for two seconds, the description will fly in from the left.
     */
    private fun isTriggerHovered() = getMouseX() in x..x + 5 && getMouseY() in y..y + height

    /**
     * Extension to [drawEntry].
     */
    abstract fun drawContent(x: Int, y: Int, height: Int, width: Int)

    /**
     * Called when the entry is (double-) clicked.
     */
    override fun clicked(isDoubleClick: Boolean, mouseOnEntryX: Int, mouseOnEntryY: Int, entryWidth: Int, entryHeight: Int)
    {
    }

    /**
     * Removes the selection effect.
     */
    override fun drawSelectionEffect(left: Int, right: Int, topY: Int, targetHeight: Int)
    {
    }
}