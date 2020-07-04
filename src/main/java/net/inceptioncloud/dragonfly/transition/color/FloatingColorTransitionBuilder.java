package net.inceptioncloud.dragonfly.transition.color;

import java.awt.*;
import java.util.function.IntSupplier;

public class FloatingColorTransitionBuilder
{
    private Color start;
    private Color end;
    private int amountOfSteps;
    private Runnable reachEnd;
    private Runnable reachStart;
    private IntSupplier autoTransformator;

    public FloatingColorTransitionBuilder start (final Color start)
    {
        this.start = start;
        return this;
    }

    public FloatingColorTransitionBuilder end (final Color end)
    {
        this.end = end;
        return this;
    }

    public FloatingColorTransitionBuilder amountOfSteps (final int amountOfSteps)
    {
        this.amountOfSteps = amountOfSteps;
        return this;
    }

    public FloatingColorTransitionBuilder reachEnd (final Runnable reachEnd)
    {
        this.reachEnd = reachEnd;
        return this;
    }

    public FloatingColorTransitionBuilder reachStart (final Runnable reachStart)
    {
        this.reachStart = reachStart;
        return this;
    }

    public FloatingColorTransitionBuilder autoTransformator (final IntSupplier autoTransformator)
    {
        this.autoTransformator = autoTransformator;
        return this;
    }

    public FloatingColorTransition createFloatingColorTransition ()
    {
        return new FloatingColorTransition(start, end, amountOfSteps, reachEnd, reachStart, autoTransformator);
    }
}