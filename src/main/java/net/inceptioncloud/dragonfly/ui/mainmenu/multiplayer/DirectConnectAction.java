package net.inceptioncloud.dragonfly.ui.mainmenu.multiplayer;

import net.inceptioncloud.dragonfly.ui.mainmenu.QuickAction;
import net.minecraft.client.gui.GuiDirectConnect;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;

/**
 * Opens the direct connect menu to join a server.
 */
public class DirectConnectAction extends QuickAction
{
    /**
     * Default Constructor
     */
    public DirectConnectAction ()
    {
        super(1, 14, "Direct Connect",
            () ->
            {
                GuiMultiplayer guiMultiplayer = new GuiMultiplayer(mc.currentScreen);
                mc.displayGuiScreen(guiMultiplayer);
                guiMultiplayer.directConnect = true;
                mc.displayGuiScreen(new GuiDirectConnect(guiMultiplayer, guiMultiplayer.selectedServer = new ServerData(I18n.format("selectServer.defaultName"), "", false)));
            });
    }
}
