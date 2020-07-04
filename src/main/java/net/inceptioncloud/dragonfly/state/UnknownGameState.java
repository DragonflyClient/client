package net.inceptioncloud.dragonfly.state;

import net.inceptioncloud.dragonfly.discord.RichPresenceAdapter;

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
