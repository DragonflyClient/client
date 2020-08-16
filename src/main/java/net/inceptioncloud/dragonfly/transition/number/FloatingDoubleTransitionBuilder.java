package net.inceptioncloud.dragonfly.transition.number;

import java.util.function.IntSupplier;

public class FloatingDoubleTransitionBuilder {
    private double start;
    private double end;
    private int amountOfSteps;
    private Runnable reachEnd;
    private Runnable reachStart;
    private IntSupplier autoTransformator;

    public FloatingDoubleTransitionBuilder start(final double start) {
        this.start = start;
        return this;
    }

    public FloatingDoubleTransitionBuilder end(final double end) {
        this.end = end;
        return this;
    }

    public FloatingDoubleTransitionBuilder amountOfSteps(final int amountOfSteps) {
        this.amountOfSteps = amountOfSteps;
        return this;
    }

    public FloatingDoubleTransitionBuilder reachEnd(final Runnable reachEnd) {
        this.reachEnd = reachEnd;
        return this;
    }

    public FloatingDoubleTransitionBuilder reachStart(final Runnable reachStart) {
        this.reachStart = reachStart;
        return this;
    }

    public FloatingDoubleTransitionBuilder autoTransformator(final IntSupplier autoTransformator) {
        this.autoTransformator = autoTransformator;
        return this;
    }

    public FloatingDoubleTransition createFloatingDoubleTransition() {
        return new FloatingDoubleTransition(start, end, amountOfSteps, reachEnd, reachStart, autoTransformator);
    }
}