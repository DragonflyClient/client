package net.inceptioncloud.minecraftmod.ui.mainmenu.quickactions.options;

import net.inceptioncloud.minecraftmod.ui.mainmenu.quickactions.QuickAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenResourcePacks;

/**
 * Opens the resource pack selection when clicking on this quick action.
 */
public class ResourcePackAction extends QuickAction
{
    /**
     * Default Constructor.
     */
    public ResourcePackAction ()
    {
        super(2, 15, "Resource Packs", () -> Minecraft.getMinecraft().displayGuiScreen(new GuiScreenResourcePacks(Minecraft.getMinecraft().currentScreen)));
    }
}
