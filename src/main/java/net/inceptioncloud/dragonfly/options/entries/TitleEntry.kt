package net.inceptioncloud.dragonfly.options.entries

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.BluePalette
import net.inceptioncloud.dragonfly.ui.components.list.UIListEntry
import net.minecraft.client.gui.Gui

class TitleEntry(val string: String) : UIListEntry()
{
    /**
     * Called when the entry is drawn at a certain position with the given height and with.
     */
    override fun drawEntry(x: Int, y: Int, height: Int, width: Int)
    {
        Gui.drawRect(x, y, x + width, y + height, BluePalette.PRIMARY_LIGHT.rgb)
        Dragonfly.fontManager.subtitle.drawCenteredString(string, x + width / 2, y + height / 2 - 3, BluePalette.BACKGROUND.rgb, false)
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
