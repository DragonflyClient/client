package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;

public abstract class GuiListExtended extends GuiSlot
{
    /**
     * Initializes a new Gui List.
     */
    public GuiListExtended (Minecraft mc, int width, int height, int top, int bottom, int slotHeight)
    {
        super(mc, width, height, top, bottom, slotHeight);
    }

    /**
     * Called when an element in the list is clicked.
     *
     * @param slotIndex     Index of the element
     * @param isDoubleClick Whether it was double-clicked
     */
    protected void elementClicked (int slotIndex, boolean isDoubleClick, int mouseX, int mouseY)
    {
    }

    /**
     * Returns true if the element passed in is currently selected.
     */
    protected boolean isSelected (int slotIndex)
    {
        return false;
    }

    /**
     * Disables the default background drawing.
     */
    protected void drawBackground ()
    {
    }

    protected void drawSlot (int entryID, int x, int y, int height, int mouseXIn, int mouseYIn)
    {
        this.getListEntry(entryID).drawEntry(entryID, x, y, this.getListWidth(), height, mouseXIn, mouseYIn, this.getSlotIndexFromScreenCoords(mouseXIn, mouseYIn) == entryID);
    }

    protected void updateItemPos (int entryID, int x, int y)
    {
        this.getListEntry(entryID).setSelected(entryID, x, y);
    }

    public boolean mouseClicked (int mouseX, int mouseY, int mouseEvent)
    {
        if (this.isMouseYWithinSlotBounds(mouseY)) {
            int i = this.getSlotIndexFromScreenCoords(mouseX, mouseY);

            if (i >= 0) {
                int j = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
                int k = this.top + 4 - this.getAmountScrolled() + i * this.entryHeight + this.headerPadding;
                int l = mouseX - j;
                int i1 = mouseY - k;

                if (this.getListEntry(i).mousePressed(i, mouseX, mouseY, mouseEvent, l, i1)) {
                    this.setEnabled(false);
                    return true;
                }
            }
        }

        return false;
    }

    public boolean mouseReleased (int mouseX, int mouseY, int mouseEvent)
    {
        for (int i = 0 ; i < this.getSize() ; ++i) {
            int j = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
            int k = this.top + 4 - this.getAmountScrolled() + i * this.entryHeight + this.headerPadding;
            int l = mouseX - j;
            int i1 = mouseY - k;
            this.getListEntry(i).mouseReleased(i, mouseX, mouseY, mouseEvent, l, i1);
        }

        this.setEnabled(true);
        return false;
    }

    /**
     * Gets the IGuiListEntry object for the given index
     */
    public abstract GuiListExtended.IGuiListEntry getListEntry (int index);

    public interface IGuiListEntry
    {
        void setSelected (int entryID, int insideLeft, int yPos);

        void drawEntry (int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected);

        boolean mousePressed (int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY);

        void mouseReleased (int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY);
    }
}
