package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;

public abstract class GuiResourcePackList extends GuiListExtended
{
    protected final Minecraft mc;
    protected final List<ResourcePackListEntry> list;

    public GuiResourcePackList(Minecraft mcIn, int p_i45055_2_, int p_i45055_3_, List<ResourcePackListEntry> p_i45055_4_)
    {
        super(mcIn, p_i45055_2_, p_i45055_3_, 32, p_i45055_3_ - 55 + 4, 36);
        this.mc = mcIn;
        this.list = p_i45055_4_;
        this.field_148163_i = false;
        this.setHasListHeader(true, (int)((float)mcIn.fontRendererObj.FONT_HEIGHT * 1.5F));
    }

    /**
     * Handles drawing a list's header row.
     */
    protected void drawListHeader(int x, int y, Tessellator tessellator)
    {
        String s = EnumChatFormatting.UNDERLINE + "" + EnumChatFormatting.BOLD + this.getListHeader();
        this.mc.fontRendererObj.drawString(s, x + this.width / 2 - this.mc.fontRendererObj.getStringWidth(s) / 2, Math.min(this.top + 3, y), 16777215);
    }

    protected abstract String getListHeader();

    public List<ResourcePackListEntry> getList()
    {
        return this.list;
    }

    protected int getSize()
    {
        return this.getList().size();
    }

    /**
     * Gets the IGuiListEntry object for the given index
     */
    public ResourcePackListEntry getListEntry(int index)
    {
        return this.getList().get(index);
    }

    /**
     * Gets the width of the list
     */
    public int getListWidth()
    {
        return this.width;
    }

    protected int getScrollBarX()
    {
        return this.right - 6;
    }
}