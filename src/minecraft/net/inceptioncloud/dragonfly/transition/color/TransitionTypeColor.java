package net.inceptioncloud.dragonfly.transition.color;

import net.inceptioncloud.dragonfly.transition.Transition;

import java.awt.*;
import java.util.function.IntSupplier;

/**
 * The superclass of any transition that supplies a color.
 */
public abstract class TransitionTypeColor extends Transition
{
    /**
     * Create a new color transition.
     *
     * @param reachEnd {@link #reachEnd}
     * @param reachStart {@link #reachStart}
     */
    public TransitionTypeColor (final Runnable reachEnd, final Runnable reachStart, final IntSupplier autoTransformator)
    {
        super(reachEnd, reachStart, autoTransformator);
    }

    /**
     * @return The current color value
     */
    public abstract Color get ();
}
