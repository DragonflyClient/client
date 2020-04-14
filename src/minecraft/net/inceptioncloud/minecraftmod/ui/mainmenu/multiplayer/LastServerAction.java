package net.inceptioncloud.minecraftmod.ui.mainmenu.multiplayer;

import net.inceptioncloud.minecraftmod.options.sections.StorageOptions;
import net.inceptioncloud.minecraftmod.ui.mainmenu.QuickAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.EnumChatFormatting;

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
        super(1, 13, getName().orElse("-/-"), () -> getIP().ifPresent(lastIP ->
        {
            final String address = lastIP.contains(":") ? lastIP.split(":")[0] : lastIP;
            final int port = lastIP.contains(":") ? Integer.parseInt(lastIP.split(":")[1]) : 25565;
            final ServerData serverData = new ServerData(getName().orElse("Minecraft Server"), address + ":" + port, false);

            mc.displayGuiScreen(new GuiConnecting(mc.currentScreen, Minecraft.getMinecraft(), serverData));
        }));
    }

    /**
     * @return The IP-Address of the last visited server. (Nullable)
     */
    private static Optional<String> getIP ()
    {
        return StorageOptions.LAST_SERVER.get() != null
            ? Optional.of(StorageOptions.LAST_SERVER.get().serverIP)
            : Optional.empty();
    }

    /**
     * @return The name if the last visited server. Can be equal to the server IP-Address. (Nullable)
     */
    private static Optional<String> getName ()
    {
        return StorageOptions.LAST_SERVER.get() != null
            ? Optional.of(EnumChatFormatting.getTextWithoutFormattingCodes(StorageOptions.LAST_SERVER.get().serverName))
            : Optional.empty();
    }
}
