package net.inceptioncloud.minecraftmod.transition.number;

import net.inceptioncloud.minecraftmod.transition.Transition;

import java.util.function.IntSupplier;

/**
 * The superclass of any transition that supplies numbers.
 */
public abstract class TransitionTypeNumber extends Transition
{
    /**
     * Create a new number transition.
     *
     * @param reachEnd   {@link #reachEnd}
     * @param reachStart {@link #reachStart}
     */
    public TransitionTypeNumber (final Runnable reachEnd, final Runnable reachStart, final IntSupplier autoTransformator)
    {
        super(reachEnd, reachStart, autoTransformator);
    }

    /**
     * @return The current value.
     */
    public abstract double get ();

    /**
     * @return The current value casted to an integer.
     */
    public abstract int castToInt ();
}
