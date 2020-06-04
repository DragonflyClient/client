package net.inceptioncloud.minecraftmod.state.menu;

import net.inceptioncloud.minecraftmod.discord.RichPresenceAdapter;
import net.inceptioncloud.minecraftmod.discord.custom.MenuRPC;
import net.inceptioncloud.minecraftmod.state.GameState;
import net.inceptioncloud.minecraftmod.state.play.PlayingState;

/**
 * The opposite of the {@link PlayingState} super-gamestate.
 * Selected if the user isn't ingame but in a menu.
 */
public class MenuState extends GameState
{
    /**
     * @return The {@link RichPresenceAdapter} that belongs to this Game State.
     */
    @Override
    public RichPresenceAdapter getBelongingRichPresence ()
    {
        return new MenuRPC();
    }

    @Override
    public String toString ()
    {
        return "MenuState{}";
    }
}
