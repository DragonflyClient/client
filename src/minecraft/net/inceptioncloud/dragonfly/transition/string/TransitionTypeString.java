package net.inceptioncloud.dragonfly.transition.string;

import net.inceptioncloud.dragonfly.transition.Transition;

import java.util.function.IntSupplier;

/**
 * The superclass of any transition that supplies numbers.
 */
public abstract class TransitionTypeString extends Transition
{
    /**
     * Create a new number transition.
     *
     * @param reachEnd   {@link #reachEnd}
     * @param reachStart {@link #reachStart}
     */
    public TransitionTypeString (final Runnable reachEnd, final Runnable reachStart, final IntSupplier autoTransformator)
    {
        super(reachEnd, reachStart, autoTransformator);
    }

    /**
     * @return The current value.
     */
    public abstract String get ();
}
