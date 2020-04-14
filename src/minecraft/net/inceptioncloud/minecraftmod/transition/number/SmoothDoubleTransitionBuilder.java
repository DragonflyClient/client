package net.inceptioncloud.minecraftmod.transition.number;

import java.util.function.IntSupplier;

public class SmoothDoubleTransitionBuilder
{
    private double start;
    private double end;
    private int fadeIn;
    private int stay;
    private int fadeOut;
    private Runnable reachEnd;
    private Runnable reachStart;
    private IntSupplier autoTransformator;

    public SmoothDoubleTransitionBuilder start (final double start)
    {
        this.start = start;
        return this;
    }

    public SmoothDoubleTransitionBuilder end (final double end)
    {
        this.end = end;
        return this;
    }

    public SmoothDoubleTransitionBuilder fadeIn (final int fadeIn)
    {
        this.fadeIn = fadeIn;
        return this;
    }

    public SmoothDoubleTransitionBuilder stay (final int stay)
    {
        this.stay = stay;
        return this;
    }

    public SmoothDoubleTransitionBuilder fadeOut (final int fadeOut)
    {
        this.fadeOut = fadeOut;
        return this;
    }

    public SmoothDoubleTransitionBuilder reachEnd (final Runnable reachEnd)
    {
        this.reachEnd = reachEnd;
        return this;
    }

    public SmoothDoubleTransitionBuilder reachStart (final Runnable reachStart)
    {
        this.reachStart = reachStart;
        return this;
    }

    public SmoothDoubleTransitionBuilder autoTransformator (final IntSupplier autoTransformator)
    {
        this.autoTransformator = autoTransformator;
        return this;
    }

    public SmoothDoubleTransition build ()
    {
        return new SmoothDoubleTransition(start, end, fadeIn, stay, fadeOut, reachEnd, reachStart, autoTransformator);
    }
}