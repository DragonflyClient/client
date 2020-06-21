package net.inceptioncloud.minecraftmod.ui.mainmenu.options;

import net.inceptioncloud.minecraftmod.ui.mainmenu.QuickAction;
import net.inceptioncloud.minecraftmod.ui.screens.ModOptionsUI;

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
