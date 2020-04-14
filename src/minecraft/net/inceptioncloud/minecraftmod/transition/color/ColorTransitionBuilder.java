package net.inceptioncloud.minecraftmod.transition.color;

import java.awt.*;
import java.util.function.IntSupplier;

public class ColorTransitionBuilder
{
    private Color start;
    private Color end;
    private int amountOfSteps;
    private Runnable reachEnd;
    private Runnable reachStart;
    private IntSupplier autoTransformator;

    public ColorTransitionBuilder start (final Color start)
    {
        this.start = start;
        return this;
    }

    public ColorTransitionBuilder end (final Color end)
    {
        this.end = end;
        return this;
    }

    public ColorTransitionBuilder amountOfSteps (final int amountOfSteps)
    {
        this.amountOfSteps = amountOfSteps;
        return this;
    }

    public ColorTransitionBuilder reachEnd (final Runnable reachEnd)
    {
        this.reachEnd = reachEnd;
        return this;
    }

    public ColorTransitionBuilder reachStart (final Runnable reachStart)
    {
        this.reachStart = reachStart;
        return this;
    }

    public ColorTransitionBuilder autoTransformator (final IntSupplier autoTransformator)
    {
        this.autoTransformator = autoTransformator;
        return this;
    }

    public ColorTransition build ()
    {
        return new ColorTransition(start, end, amountOfSteps, reachEnd, reachStart, autoTransformator);
    }
}