package net.inceptioncloud.minecraftmod.state.menu.subscriber;

import com.google.common.eventbus.Subscribe;
import net.inceptioncloud.minecraftmod.Dragonfly;
import net.inceptioncloud.minecraftmod.event.gui.GuiScreenDisplayEvent;
import net.inceptioncloud.minecraftmod.state.menu.MenuState;
import net.inceptioncloud.minecraftmod.state.play.PlayingState;
import net.minecraft.client.Minecraft;

/**
 * The subscriber that performs the changing to the {@link MenuState}.
 */
public class MenuSubscriber
{
    /**
     * Updates the current Game State to {@link MenuState} if the user is not in-game and if the previous
     * screen was an {@link PlayingState}.
     */
    @Subscribe
    public void guiScreenDisplay (GuiScreenDisplayEvent event)
    {
        if(Minecraft.getMinecraft().theWorld != null)
            return;

        Dragonfly.getGameStateManager().getCurrent().ifPlaying(playingState ->
            Dragonfly.getGameStateManager().updateState(new MenuState())
        );
    }
}
