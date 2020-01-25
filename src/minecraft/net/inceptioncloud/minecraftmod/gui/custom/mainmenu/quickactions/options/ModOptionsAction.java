package net.inceptioncloud.minecraftmod.gui.custom.mainmenu.quickactions.options;

import net.inceptioncloud.minecraftmod.gui.custom.mainmenu.quickactions.QuickAction;

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
        super(2, 16, "Mod Options", () ->
        {
            // TODO [25.01.2020]: Switch to Mod Options
        });
    }
}
