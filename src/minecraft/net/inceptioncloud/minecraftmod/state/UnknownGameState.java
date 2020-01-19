package net.inceptioncloud.minecraftmod.state;

import net.inceptioncloud.minecraftmod.discord.RichPresenceStatus;

/**
 * The default loading Game State.
 */
public class UnknownGameState extends GameState
{
    /**
     * @return The {@link RichPresenceStatus} that belongs to this Game State.
     */
    @Override
    public RichPresenceStatus getBelongingRichPresence ()
    {
        return null;
    }
}
