package net.inceptioncloud.dragonfly.transition.number;

import java.util.function.IntSupplier;

public class DoubleTransitionBuilder {
    private double start;
    private double end;
    private int amountOfSteps;
    private Runnable reachEnd;
    private Runnable reachStart;
    private IntSupplier autoTransformator;

    public DoubleTransitionBuilder start(final double start) {
        this.start = start;
        return this;
    }

    public DoubleTransitionBuilder end(final double end) {
        this.end = end;
        return this;
    }

    public DoubleTransitionBuilder amountOfSteps(final int amountOfSteps) {
        this.amountOfSteps = amountOfSteps;
        return this;
    }

    public DoubleTransitionBuilder reachEnd(final Runnable reachEnd) {
        this.reachEnd = reachEnd;
        return this;
    }

    public DoubleTransitionBuilder reachStart(final Runnable reachStart) {
        this.reachStart = reachStart;
        return this;
    }

    public DoubleTransitionBuilder autoTransformator(final IntSupplier autoTransformator) {
        this.autoTransformator = autoTransformator;
        return this;
    }

    public DoubleTransition build() {
        return new DoubleTransition(start, end, amountOfSteps, reachEnd, reachStart, autoTransformator);
    }
}