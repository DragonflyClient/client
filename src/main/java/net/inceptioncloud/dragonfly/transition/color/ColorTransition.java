package net.inceptioncloud.dragonfly.transition.color;

import net.inceptioncloud.dragonfly.transition.number.DoubleTransition;
import org.apache.commons.lang3.Validate;

import java.awt.*;
import java.util.function.IntSupplier;

/**
 * <h2>Color Transition</h2>
 * <p>
 * Supplies a color value that transforms from the start value to the end value on only that direction.
 */
public class ColorTransition extends TransitionTypeColor {
    /**
     * A simple object that the constructor and non-thread-safe methods are synchronized on.
     * The content of this object is never used and it is never updated or accessed.
     *
     * @since v1.0.1.0 ~ hotfix/transition-thread-safe
     */
    private final Object threadLock = new Object();

    /**
     * The color with which the transition starts.
     */
    protected Color start;

    /**
     * The color with which the transition ends.
     */
    protected Color end;

    /**
     * The base transition for the red value.
     */
    protected DoubleTransition redBase;

    /**
     * The base transition for the green value.
     */
    protected DoubleTransition greenBase;

    /**
     * The base transition for the blue value.
     */
    protected DoubleTransition blueBase;

    /**
     * Constructor-Variable Field
     */
    protected int amountOfSteps;

    /**
     * Create a new color transition instance.
     *
     * @param start         {@link #start}
     * @param end           {@link #end}
     * @param amountOfSteps The amount of steps
     * @param reachEnd      {@link #reachEnd}
     * @param reachStart    {@link #reachStart}
     */
    ColorTransition(
            final Color start, final Color end, final int amountOfSteps,
            final Runnable reachEnd, final Runnable reachStart, final IntSupplier autoTransformator
    ) {
        super(reachEnd, reachStart, autoTransformator);

        synchronized (threadLock) {
            Validate.isTrue(!start.equals(end), "Start and end value cannot be both the same");

            this.start = start;
            this.end = end;
            this.amountOfSteps = amountOfSteps;

            this.redBase = DoubleTransition.builder().start(start.getRed()).end(end.getRed()).amountOfSteps(amountOfSteps).build();
            this.greenBase = DoubleTransition.builder().start(start.getGreen()).end(end.getGreen()).amountOfSteps(amountOfSteps).build();
            this.blueBase = DoubleTransition.builder().start(start.getBlue()).end(end.getBlue()).amountOfSteps(amountOfSteps).build();
        }
    }

    public static ColorTransitionBuilder builder() {
        return new ColorTransitionBuilder();
    }

    /**
     * @return The current color value
     */
    @Override
    public Color get() {
        return new Color(redBase.castToInt(), greenBase.castToInt(), blueBase.castToInt());
    }

    /**
     * @return Whether the current value is at the end.
     */
    @Override
    public boolean isAtEnd() {
        return redBase.isAtEnd() && greenBase.isAtEnd() && blueBase.isAtEnd();
    }

    /**
     * @return Whether the current value is at the start.
     */
    @Override
    public boolean isAtStart() {
        return redBase != null && redBase.isAtStart()
                && greenBase != null && greenBase.isAtStart()
                && blueBase != null && blueBase.isAtStart();
    }

    /**
     * The step-forward method for the transition.
     */
    @Override
    public void doForward() {
        synchronized (threadLock) {
            try {
                redBase.setForward();
                greenBase.setForward();
                blueBase.setForward();

                if (isAtEnd() && reachEnd != null)
                    reachEnd.run();
            } catch (NullPointerException exception) {
                exception.printStackTrace();
            }
        }
    }

    /**
     * The step-backward method for the transition.
     */
    @Override
    public void doBackward() {
        synchronized (threadLock) {
            if (redBase != null && greenBase != null && blueBase != null) {
                redBase.setBackward();
                greenBase.setBackward();
                blueBase.setBackward();
            }

            if (isAtStart() && reachStart != null)
                reachStart.run();
        }
    }

    @Override
    public void destroy() {
        redBase.destroy();
        greenBase.destroy();
        blueBase.destroy();

        super.destroy();
    }

    @Override
    public String toString() {
        return "ColorTransition{" +
                "originStackTrace=" + originStackTrace +
                '}';
    }
}
