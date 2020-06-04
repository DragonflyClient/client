package net.inceptioncloud.minecraftmod.transition.number;

import java.util.function.IntSupplier;

public class OverflowDoubleTransitionBuilder
{
    private double start;
    private double overflow;
    private double end;
    private int firstAmount;
    private int secondAmount;
    private Runnable reachEnd;
    private Runnable reachStart;
    private IntSupplier autoTransformator;

    public OverflowDoubleTransitionBuilder start (final double start)
    {
        this.start = start;
        return this;
    }

    public OverflowDoubleTransitionBuilder overflow (final double overflow)
    {
        this.overflow = overflow;
        return this;
    }

    public OverflowDoubleTransitionBuilder end (final double end)
    {
        this.end = end;
        return this;
    }

    public OverflowDoubleTransitionBuilder firstAmount (final int firstAmount)
    {
        this.firstAmount = firstAmount;
        return this;
    }

    public OverflowDoubleTransitionBuilder secondAmount (final int secondAmount)
    {
        this.secondAmount = secondAmount;
        return this;
    }

    public OverflowDoubleTransitionBuilder reachEnd (final Runnable reachEnd)
    {
        this.reachEnd = reachEnd;
        return this;
    }

    public OverflowDoubleTransitionBuilder reachStart (final Runnable reachStart)
    {
        this.reachStart = reachStart;
        return this;
    }

    public OverflowDoubleTransitionBuilder autoTransformator (final IntSupplier autoTransformator)
    {
        this.autoTransformator = autoTransformator;
        return this;
    }

    public OverflowDoubleTransition createOverflowDoubleTransition ()
    {
        return new OverflowDoubleTransition(start, overflow, end, firstAmount, secondAmount, reachEnd, reachStart, autoTransformator);
    }
}