package net.inceptioncloud.dragonfly.state;

import net.inceptioncloud.dragonfly.discord.RichPresenceAdapter;
import net.inceptioncloud.dragonfly.state.play.MultiplayerState;
import net.inceptioncloud.dragonfly.state.play.PlayingState;
import net.inceptioncloud.dragonfly.state.play.SingleplayerState;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Superclass of all different game states.
 */
public abstract class GameState
{
    /**
     * @return The {@link RichPresenceAdapter} that belongs to this Game State.
     */
    public abstract RichPresenceAdapter getBelongingRichPresence ();

    /**
     * If this Game State is a {@link SingleplayerState}, the consumer will be called with the casted value.
     *
     * @param consumer The consumer to handle the value
     */
    public final void ifSingleplayer (Consumer<SingleplayerState> consumer)
    {
        if (this instanceof SingleplayerState)
            consumer.accept((SingleplayerState) this);
    }

    /**
     * If this Game State is a {@link MultiplayerState}, the consumer will be called with the casted value.
     *
     * @param consumer The consumer to handle the value
     */
    public final void ifMultiplayer (Consumer<MultiplayerState> consumer)
    {
        if (this instanceof MultiplayerState)
            consumer.accept((MultiplayerState) this);
    }

    /**
     * If this Game State is a {@link PlayingState}, the consumer will be called with the casted value.
     *
     * @param consumer The consumer to handle the value
     */
    public final void ifPlaying (Consumer<PlayingState> consumer)
    {
        if (this instanceof PlayingState)
            consumer.accept((PlayingState) this);
    }

    /**
     * If this Game State is a {@link SingleplayerState}, call the function to get a value.
     * Otherwise return the callback value.
     *
     * @param getter   The function to get the value if the Game State matches
     * @param callback The callback value
     * @param <T>      The type of the value
     *
     * @return The value
     */
    public final <T> T ifSingleplayer (Function<SingleplayerState, T> getter, T callback)
    {
        return this instanceof SingleplayerState ? getter.apply(( SingleplayerState ) this) : callback;
    }

    /**
     * If this Game State is a {@link MultiplayerState}, call the function to get a value.
     * Otherwise return the callback value.
     *
     * @param getter   The function to get the value if the Game State matches
     * @param callback The callback value
     * @param <T>      The type of the value
     *
     * @return The value
     */
    public final <T> T ifMultiplayer (Function<MultiplayerState, T> getter, T callback)
    {
        return this instanceof MultiplayerState ? getter.apply(( MultiplayerState ) this) : callback;
    }

    /**
     * If this Game State is a {@link PlayingState}, call the function to get a value.
     * Otherwise return the callback value.
     *
     * @param getter   The function to get the value if the Game State matches
     * @param callback The callback value
     * @param <T>      The type of the value
     *
     * @return The value
     */
    public final <T> T ifPlaying (Function<PlayingState, T> getter, T callback)
    {
        return this instanceof PlayingState ? getter.apply(( PlayingState ) this) : callback;
    }
}
