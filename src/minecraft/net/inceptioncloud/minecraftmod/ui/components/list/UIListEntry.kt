package net.inceptioncloud.minecraftmod.ui.components.list

import net.inceptioncloud.minecraftmod.design.color.BluePalette
import net.inceptioncloud.minecraftmod.design.color.RGB
import net.minecraft.client.gui.Gui

/**
 * Represents an entry in an [UIList].
 */
abstract class UIListEntry
{
    /**
     * Whether the entry is currently selected.
     */
    var selected: Boolean = false

    /**
     * Last x-coordinate on which the entry was drawn.
     */
    var x: Int = 0

    /**
     * Last y-coordinate on which the entry was drawn.
     */
    var y: Int = 0

    /**
     * Called to render an effect that indicates that the entry is currently selected.
     * Only called if [selected] is true. Can be kept empty to render no effect.
     */
    open fun drawSelectionEffect(left: Int, right: Int, topY: Int, targetHeight: Int)
    {
        Gui.drawRect(left, topY, right, topY + targetHeight,
                RGB.of(BluePalette.FOREGROUND).alpha(0.2F).rgb())
    }

    /**
     * Called when the selection state ([selected]) of the entry changes.
     */
    fun updateSelectionState(selected: Boolean)
    {
        this.selected = selected
    }

    /**
     * Caches the location of the entry when drawing.
     */
    fun cacheLocation(x: Int, y: Int)
    {
        this.x = x
        this.y = y
    }

    /**
     * Called when the entry is drawn at a certain position with the given height and with.
     */
    abstract fun drawEntry(x: Int, y: Int, height: Int, width: Int)

    /**
     * Called when the entry is (double-) clicked.
     */
    abstract fun clicked(isDoubleClick: Boolean, mouseOnEntryX: Int, mouseOnEntryY: Int, entryWidth: Int,
                         entryHeight: Int)

    open fun mouseDragged(mouseX: Int, mouseY: Int, eventButton: Int, duration: Long)
    {
    }

    open fun mouseReleased(mouseX: Int, mouseY: Int, eventButton: Int)
    {
    }

    open fun mousePressed(mouseX: Int, mouseY: Int, eventButton: Int)
    {
    }
}