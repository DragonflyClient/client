package net.inceptioncloud.minecraftmod.gui.mainmenu.quickactions.options;

import net.inceptioncloud.minecraftmod.gui.mainmenu.quickactions.QuickAction;
import net.inceptioncloud.minecraftmod.gui.options.GuiModOptions;
import net.minecraft.client.Minecraft;

/**
 * Switches to the Mod Options GUI when clicking on this quick action.
 */
public class ModOptionsAction extends QuickAction
{
    /**
     * Default Constructor
     */
    public ModOptionsAction ()
    {
        super(2, 16, "Mod Options", () -> Minecraft.getMinecraft().displayGuiScreen(new GuiModOptions()));
    }
}
