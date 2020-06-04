package net.inceptioncloud.minecraftmod.ui.mainmenu.quit;

import net.inceptioncloud.minecraftmod.Dragonfly;
import net.inceptioncloud.minecraftmod.ui.mainmenu.QuickAction;

/**
 * Provides the ability to reload any client feature without restarting the client.
 */
public class ReloadAction extends QuickAction
{
    /**
     * Default Constructor
     */
    public ReloadAction ()
    {
        super(3, 17, "Reload", () -> Dragonfly.reload());
    }
}
