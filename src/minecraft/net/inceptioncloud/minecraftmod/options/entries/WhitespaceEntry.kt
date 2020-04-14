package net.inceptioncloud.minecraftmod.options.entries

import net.inceptioncloud.minecraftmod.ui.components.list.UIListEntry

class WhitespaceEntry : UIListEntry()
{
    /**
     * Called when the entry is drawn at a certain position with the given height and with.
     */
    override fun drawEntry(x: Int, y: Int, height: Int, width: Int)
    {
    }

    /**
     * Called when the entry is (double-) clicked.
     */
    override fun clicked(isDoubleClick: Boolean, mouseOnEntryX: Int, mouseOnEntryY: Int, entryWidth: Int, entryHeight: Int)
    {
    }

    override fun drawSelectionEffect(left: Int, right: Int, topY: Int, targetHeight: Int)
    {
    }
}