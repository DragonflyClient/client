package net.inceptioncloud.minecraftmod.state.play.subscriber;

import com.google.common.eventbus.Subscribe;
import net.inceptioncloud.minecraftmod.Dragonfly;
import net.inceptioncloud.minecraftmod.event.gui.GuiScreenDisplayEvent;
import net.inceptioncloud.minecraftmod.event.play.ServerLoggedInEvent;
import net.inceptioncloud.minecraftmod.state.play.MultiplayerState;
import net.minecraft.client.gui.GuiIngameMenu;

/**
 * The subscriber that performs the changing to the {@link MultiplayerState}.
 */
public class MultiplayerSubscriber
{
    /**
     * Updates the Game State to {@link MultiplayerState} when the user was logged in to an external server.
     */
    @Subscribe
    public void serverLoggedIn (ServerLoggedInEvent event)
    {
        Dragonfly.getGameStateManager().updateState(new MultiplayerState(false, System.currentTimeMillis(), event.getServerData()));
    }

    /**
     * Set the Game State property <code>paused</code> to <code>true</code> or <code>false</code> depending on whether the
     * new screen is <code>null</code> or not.
     */
    @Subscribe
    public void guiScreenDisplay (GuiScreenDisplayEvent event)
    {
        if (event.isCancelled()) return;
        boolean paused = event.getNewScreen() instanceof GuiIngameMenu;

        Dragonfly.getGameStateManager().getCurrent().ifMultiplayer(state ->
        {
            if (paused != state.isPaused())
                Dragonfly.getGameStateManager().updateState(new MultiplayerState(paused, state.getJoinTime(), state.getServerData()));
        });
    }
}
