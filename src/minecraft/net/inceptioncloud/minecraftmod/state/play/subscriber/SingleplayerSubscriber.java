package net.inceptioncloud.minecraftmod.state.play.subscriber;

import com.google.common.eventbus.Subscribe;
import net.inceptioncloud.minecraftmod.InceptionMod;
import net.inceptioncloud.minecraftmod.event.gui.GuiScreenDisplayEvent;
import net.inceptioncloud.minecraftmod.event.play.IntegratedServerLoggedInEvent;
import net.inceptioncloud.minecraftmod.state.play.SingleplayerState;
import net.minecraft.client.gui.GuiIngameMenu;

/**
 * The subscriber that performs the changing to the {@link SingleplayerState}.
 */
public class SingleplayerSubscriber
{
    /**
     * Updates the Game State to {@link SingleplayerState} when an integrated server was successfully started.
     */
    @Subscribe
    public void integratedServerStartup (IntegratedServerLoggedInEvent event)
    {
        InceptionMod.getInstance().getGameStateManager().updateState(new SingleplayerState(false, System.currentTimeMillis(), event.getIntegratedServer()));
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

        InceptionMod.getInstance().getGameStateManager().getCurrent().ifSingleplayer(state ->
        {
            if (paused != state.isPaused())
                InceptionMod.getInstance().getGameStateManager().updateState(new SingleplayerState(paused, state.getJoinTime(), state.getIntegratedServer()));
        });
    }
}
