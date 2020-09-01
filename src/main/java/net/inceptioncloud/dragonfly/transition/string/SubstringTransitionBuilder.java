package net.inceptioncloud.dragonfly.transition.string;

import java.util.function.IntSupplier;

public class SubstringTransitionBuilder {
    private String content;
    private int amountOfSteps;
    private Runnable reachEnd;
    private Runnable reachStart;
    private IntSupplier autoTransformator;

    public SubstringTransitionBuilder content(final String content) {
        this.content = content;
        return this;
    }

    public SubstringTransitionBuilder amountOfSteps(final int amountOfSteps) {
        this.amountOfSteps = amountOfSteps;
        return this;
    }

    public SubstringTransitionBuilder reachEnd(final Runnable reachEnd) {
        this.reachEnd = reachEnd;
        return this;
    }

    public SubstringTransitionBuilder reachStart(final Runnable reachStart) {
        this.reachStart = reachStart;
        return this;
    }

    public SubstringTransitionBuilder autoTransformator(final IntSupplier autoTransformator) {
        this.autoTransformator = autoTransformator;
        return this;
    }

    public SubstringTransition build() {
        return new SubstringTransition(content, amountOfSteps, reachEnd, reachStart, autoTransformator);
    }
}