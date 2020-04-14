package net.inceptioncloud.minecraftmod.ui.mainmenu.options;

import net.inceptioncloud.minecraftmod.ui.ModOptionsUI;
import net.inceptioncloud.minecraftmod.ui.mainmenu.QuickAction;

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
        super(2, 16, "Mod Options",
            () -> mc.displayGuiScreen(new ModOptionsUI(mc.currentScreen))
        );
    }
}
