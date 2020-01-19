package net.inceptioncloud.minecraftmod.state.menu;

import lombok.ToString;
import net.inceptioncloud.minecraftmod.discord.RichPresenceStatus;
import net.inceptioncloud.minecraftmod.discord.custom.MenuRPC;
import net.inceptioncloud.minecraftmod.state.GameState;
import net.inceptioncloud.minecraftmod.state.play.PlayingState;

/**
 * The opposite of the {@link PlayingState} super-gamestate.
 * Selected if the user isn't ingame but in a menu.
 */
@ToString
public class MenuState extends GameState
{
    /**
     * @return The {@link RichPresenceStatus} that belongs to this Game State.
     */
    @Override
    public RichPresenceStatus getBelongingRichPresence ()
    {
        return new MenuRPC();
    }
}
