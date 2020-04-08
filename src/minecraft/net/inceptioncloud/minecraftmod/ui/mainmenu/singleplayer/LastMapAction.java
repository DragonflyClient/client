package net.inceptioncloud.minecraftmod.ui.mainmenu.singleplayer;

import net.inceptioncloud.minecraftmod.ui.mainmenu.QuickAction;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.storage.SaveFormatComparator;

/**
 * This action is responsible for joining the last played world.
 */
public class LastMapAction extends QuickAction
{
    /**
     * Default Constructor
     */
    public LastMapAction ()
    {
        super(0, 11, getLastWorld() != null ? EnumChatFormatting.getTextWithoutFormattingCodes(getLastWorld().getDisplayName()) : "-/-", () ->
        {
            final SaveFormatComparator world = getLastWorld();
            if (world == null || !Minecraft.getMinecraft().getSaveLoader().canLoadWorld(world.getFileName()))
                return;

            Minecraft.getMinecraft().launchIntegratedServer(world.getFileName(), world.getDisplayName(), null);
        });
    }

    /**
     * @return The last played world.
     */
    private static SaveFormatComparator getLastWorld ()
    {
        try {
            return Minecraft.getMinecraft().getSaveLoader().getSaveList().stream().min(SaveFormatComparator::compareTo).orElse(null);
        } catch (AnvilConverterException e) {
            e.printStackTrace();
            return null;
        }
    }
}
