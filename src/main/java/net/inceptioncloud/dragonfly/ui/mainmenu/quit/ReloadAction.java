package net.inceptioncloud.dragonfly.ui.mainmenu.quit;

import net.inceptioncloud.dragonfly.Dragonfly;
import net.inceptioncloud.dragonfly.ui.mainmenu.QuickAction;

/**
 * Provides the ability to reload any client feature without restarting the client.
 */
public class ReloadAction extends QuickAction {
    /**
     * Default Constructor
     */
    public ReloadAction() {
        super(3, 17, "Reload", () -> Dragonfly.reload());
    }
}
