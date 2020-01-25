package net.inceptioncloud.minecraftmod.gui.custom.mainmenu.quickactions.singleplayer;

import net.inceptioncloud.minecraftmod.gui.custom.mainmenu.quickactions.QuickAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiCreateWorld;

/**
 * The quick action for creating a new map.
 */
public class CreateMapAction extends QuickAction
{
    /**
     * Default Constructor
     */
    public CreateMapAction ()
    {
        super(0, 12, "Create Map", () -> Minecraft.getMinecraft().displayGuiScreen(new GuiCreateWorld(Minecraft.getMinecraft().currentScreen)));
    }
}
