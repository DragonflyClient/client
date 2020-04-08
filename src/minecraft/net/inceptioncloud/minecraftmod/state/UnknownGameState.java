package net.inceptioncloud.minecraftmod.state;

import net.inceptioncloud.minecraftmod.discord.RichPresenceAdapter;

/**
 * The default loading Game State.
 */
public class UnknownGameState extends GameState
{
    /**
     * @return The {@link RichPresenceAdapter} that belongs to this Game State.
     */
    @Override
    public RichPresenceAdapter getBelongingRichPresence ()
    {
        return null;
    }
}
