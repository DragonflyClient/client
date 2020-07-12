package net.inceptioncloud.dragonfly.options.entries

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.BluePalette.BACKGROUND
import net.inceptioncloud.dragonfly.design.color.BluePalette.FOREGROUND
import net.inceptioncloud.dragonfly.design.color.RGB
import net.inceptioncloud.dragonfly.transition.number.SmoothDoubleTransition
import net.inceptioncloud.dragonfly.transition.supplier.ForwardBackward
import net.inceptioncloud.dragonfly.ui.components.list.UIListEntry
import net.inceptioncloud.dragonfly.ui.screens.ModOptionsUI
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui.*
import org.lwjgl.input.Mouse

/**
 * An abstract entry that can be used for every type of option key.
 */
abstract class OptionEntry<T>(val name: String, val description: String) : UIListEntry() {
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
     * When the condition ([isTriggerHovered]) is fulfilled, this transition will start
     * moving a background and the description text from the left over the entry.
     */
    private val transitionDescription = SmoothDoubleTransition.builder()
        .start(0.0).end(1.0)
        .fadeIn(20).stay(20).fadeOut(0)
        .autoTransformator(ForwardBackward { isTriggerHovered() })
        .build()

    /**
     * Called when the entry is drawn at a certain position with the given height and with.
     */
    final override fun drawEntry(x: Int, y: Int, height: Int, width: Int) {
        this.height = height

        drawRect(x, y, x + width, y + height, RGB.of(BACKGROUND).alpha(0.7F).rgb())
        Dragonfly.fontDesign.regular.drawString(name, x + 5 + textOffset, y + 7, FOREGROUND.rgb)

        drawContent(x, y, height, width)

        // description overlay
        drawRect(x, y, x + (width * transitionDescription.get()).toInt(), y + height, BACKGROUND.rgb)

        val fontRenderer = Dragonfly.fontDesign.defaultFont.fontRendererAsync { size = 12 }
        val lines = fontRenderer?.listFormattedStringToWidth(description, width - 10) ?: listOf()
        val centerY = y + height / 2 - 1
        var fontY = if (fontRenderer != null) centerY - (lines.size * fontRenderer.height / 2) else 0

        for (string in lines) {
            val stringWidth = fontRenderer!!.getStringWidth(string)
            fontRenderer.drawString(
                string, x + 5 - ((stringWidth + 10) * (1 - transitionDescription.get())).toInt(),
                fontY + (fontRenderer.height / 2), FOREGROUND.rgb
            )

            fontY += fontRenderer.height
        }
    }

    /**
     * Checks whether the description trigger on the left side of the entry is hovered.
     *
     * If it's hovered, a white line appears that indicates that. After the cursor stays on
     * this trigger for two seconds, the description will fly in from the left.
     */
    private fun isTriggerHovered() =
        getMouseX() in (x - 19)..(x - 5)
                && getMouseY() in (y + 3)..(y + height - 3)
                && (Minecraft.getMinecraft().currentScreen as? ModOptionsUI)?.helpAttachedEntry == this

    /**
     * Extension to [drawEntry].
     */
    abstract fun drawContent(x: Int, y: Int, height: Int, width: Int)

    /**
     * Called when the entry is (double-) clicked.
     */
    override fun clicked(isDoubleClick: Boolean, mouseOnEntryX: Int, mouseOnEntryY: Int, entryWidth: Int, entryHeight: Int) {
    }

    /**
     * Removes the selection effect.
     */
    override fun drawSelectionEffect(left: Int, right: Int, topY: Int, targetHeight: Int) {
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OptionEntry<*>

        if (name != other.name) return false
        if (description != other.description) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + description.hashCode()
        return result
    }
}