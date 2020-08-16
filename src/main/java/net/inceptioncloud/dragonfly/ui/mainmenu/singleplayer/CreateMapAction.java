package net.inceptioncloud.dragonfly.ui.mainmenu.singleplayer;

import net.inceptioncloud.dragonfly.ui.mainmenu.QuickAction;
import net.minecraft.client.gui.GuiCreateWorld;

/**
 * The quick action for creating a new map.
 */
public class CreateMapAction extends QuickAction {
    /**
     * Default Constructor
     */
    public CreateMapAction() {
        super(0, 12, "Create Map",
                () -> mc.displayGuiScreen(new GuiCreateWorld(mc.currentScreen))
        );
    }
}
