package net.minecraft.client.resources;

import net.minecraft.client.gui.GuiScreenResourcePacks;

public class ResourcePackListEntryFound extends ResourcePackListEntry
{
    private final ResourcePackRepository.Entry repositoryEntry;

    public ResourcePackListEntryFound(GuiScreenResourcePacks resourcePacksGUIIn, ResourcePackRepository.Entry p_i45053_2_)
    {
        super(resourcePacksGUIIn);
        this.repositoryEntry = p_i45053_2_;
    }

    protected void bindEntryIcon ()
    {
        this.repositoryEntry.bindTexturePackIcon(this.mc.getTextureManager());
    }

    protected int getEntryPackFormat ()
    {
        return this.repositoryEntry.getPackFormat();
    }

    protected String func_148311_a()
    {
        return this.repositoryEntry.getTexturePackDescription();
    }

    protected String func_148312_b()
    {
        return this.repositoryEntry.getResourcePackName();
    }

    public ResourcePackRepository.Entry func_148318_i()
    {
        return this.repositoryEntry;
    }
}
