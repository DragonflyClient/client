package net.inceptioncloud.minecraftmod.ui.mainmenu.quickactions.quit;

import net.inceptioncloud.minecraftmod.InceptionMod;
import net.inceptioncloud.minecraftmod.ui.mainmenu.quickactions.QuickAction;
import net.minecraft.client.Minecraft;

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
        super(3, 17, "Reload", () ->
        {
            Minecraft.getMinecraft().loadingScreen.displayLoadingString("Reloading Mod...");
            InceptionMod.create();
            Minecraft.getMinecraft().refreshResources();
        });
    }
}
