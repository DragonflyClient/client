package net.inceptioncloud.dragonfly.ui.mainmenu.options;

import net.inceptioncloud.dragonfly.ui.mainmenu.QuickAction;
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
        super(2, 15, "Resource Packs",
            () -> mc.displayGuiScreen(new GuiScreenResourcePacks(mc.currentScreen))
        );
    }
}
