package net.inceptioncloud.minecraftmod.transition.number;

import java.util.function.IntSupplier;

/**
 * <h2>Double Transition</h2>
 * <p>
 * The transition that supplies a double value that transforms from the start value to the end value
 * in only that direction.
 */
public class DoubleTransition extends TransitionTypeNumber
{
    /**
     * A simple object that the constructor and non-thread-safe methods are synchronized on.
     * The content of this object is never used and it is never updated or accessed.
     *
     * @since v1.0.1.0 ~ hotfix/transition-thread-safe
     */
    private final Object threadLock = new Object();

    /**
     * The start value.
     */
    protected double start = 10;

    /**
     * The end value.
     */
    protected double end = 100;

    /**
     * The amount the current value is changed with when processing a step.
     */
    protected double perStep = 10;

    /**
     * Whether the transition goes from positive to negative values.
     */
    protected boolean negative = false;

    /**
     * The current double value.
     */
    protected double current = 0;

    /**
     * The amount of steps to take from the start to the end.
     */
    protected int amountOfSteps;

    /**
     * The last time the value was transformed.
     */
    private long lastTransform;

    /**
     * Create a new double transition instance.
     *
     * @param start         {@link #start}
     * @param end           {@link #end}
     * @param amountOfSteps Used to calculate {@link #perStep}
     * @param reachEnd      {@link #reachEnd}
     * @param reachStart    {@link #reachStart}
     */
    DoubleTransition (final double start, final double end, final int amountOfSteps, final Runnable reachEnd, final Runnable reachStart, final IntSupplier autoTransformator)
    {
        super(reachEnd, reachStart, autoTransformator);

//        Validate.isTrue(start >= 0, "The start value has to be positive");
//        Validate.isTrue(end >= 0, "The end value has to be positive");

        synchronized (threadLock) {
            this.negative = start > end; // Check whether the double transition value is getting less when processing a forward step
            this.start = start; // Pass the start value
            this.end = end; // Pass the end value
            this.current = start; // Set the current value to the start value
            this.amountOfSteps = amountOfSteps;
            this.perStep = (Math.max(start, end) - Math.min(start, end)) / amountOfSteps; // Calculate the value with which the current value is modified when processing a step
        }
    }

    /**
     * The step-forward method for the double transition.
     */
    @Override
    public void doForward ()
    {
        synchronized (threadLock) {
            // If the value is already at the end, interrupt the step
            if (isAtEnd())
                return;

            // Process the step
            if (negative)
                current -= perStep;
            else
                current += perStep;

            lastTransform = System.currentTimeMillis();

            // If the value is at the end
            if (isAtEnd()) {
                current = end;

                // Call the runnable
                if (reachEnd != null) reachEnd.run();
            }
        }
    }

    /**
     * The step-backward method for the double transition.
     */
    @Override
    public void doBackward ()
    {
        synchronized (threadLock) {
            // If the value is already at the start, interrupt the step
            if (isAtStart())
                return;

            // Process the step
            if (negative)
                current += perStep;
            else
                current -= perStep;

            lastTransform = System.currentTimeMillis();

            // If the value is at the start
            if (isAtStart()) {
                current = start;

                // Call the runnable
                if (reachStart != null) reachStart.run();
            }
        }
    }

    /**
     * @return Whether the current value is at the end.
     */
    @Override
    public boolean isAtEnd ()
    {
        return negative ? current <= end : current >= end;
    }

    /**
     * @return Whether the current value is at the start.
     */
    @Override
    public boolean isAtStart ()
    {
        return negative ? current >= start : current <= start;
    }

    /**
     * @return The current double value
     */
    @Override
    public double get ()
    {
        return current;
    }

    /**
     * @return The current double value casted to an integer.
     */
    @Override
    public int castToInt ()
    {
        return (int) get();
    }

    public static DoubleTransitionBuilder builder ()
    {
        return new DoubleTransitionBuilder();
    }

    /**
     * Change the end value
     *
     * @param end The end value
     */
    public void setEnd (final double end)
    {
        this.end = end;
        this.perStep = (Math.max(start, end) - Math.min(start, end)) / amountOfSteps; // Calculate the value with which the current value is modified when processing a step
    }

    @Override
    public String toString ()
    {
        return "DoubleTransition{" +
               "originStackTrace=" + originStackTrace +
               '}';

    }
}
