package net.inceptioncloud.minecraftmod.gui.custom.mainmenu.quickactions.multiplayer;

import net.inceptioncloud.minecraftmod.gui.custom.mainmenu.quickactions.QuickAction;
import net.inceptioncloud.minecraftmod.options.sets.StorageOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.GuiConnecting;

import java.util.Optional;

/**
 * Join the last visited server when clicking this action.
 */
public class LastServerAction extends QuickAction
{
    /**
     * Default Constructor
     */
    public LastServerAction ()
    {
        super(1, 13, getLastServer().orElse("-/-"), () -> getLastServer().ifPresent(lastServer ->
        {
            final String address = lastServer.contains(":") ? lastServer.split(":")[0] : lastServer;
            final int port = lastServer.contains(":") ? Integer.parseInt(lastServer.split(":")[1]) : 25565;
            Minecraft.getMinecraft().displayGuiScreen(new GuiConnecting(Minecraft.getMinecraft().currentScreen, Minecraft.getMinecraft(), address, port));
        }));
    }

    /**
     * @return The IP-Address if the last visited server. (Nullable)
     */
    private static Optional<String> getLastServer ()
    {
        return StorageOptions.LAST_SERVER.get() != null ? Optional.of(StorageOptions.LAST_SERVER.get()) : Optional.empty();
    }
}
