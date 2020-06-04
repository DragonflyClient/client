package net.inceptioncloud.minecraftmod.transition.number;

import org.apache.commons.lang3.Validate;

import java.util.function.IntSupplier;

/**
 * <h2>Overflow Double Transition</h2>
 * <p>
 * Supplies a double value that transforms from the start value to the overflow value and then from the overflow
 * value to the end value in both directions.
 */
public class OverflowDoubleTransition extends TransitionTypeNumber
{
    /**
     * A simple object that the constructor and non-thread-safe methods are synchronized on.
     * The content of this object is never used and it is never updated or accessed.
     *
     * @since v1.0.1.0 ~ hotfix/transition-thread-safe
     */
    private final Object threadLock = new Object();

    /**
     * The first transition that goes from the start value to the between value.
     */
    private final DoubleTransition first;

    /**
     * The second transition that goes from the between value to the end value.
     */
    private final DoubleTransition second;

    /**
     * Whether the current transition is the first one.
     */
    private boolean currentlyFirst = true;

    /**
     * Create a new overflow double transition.
     *
     * @param start        The start value
     * @param overflow     The break overflow value
     * @param end          The end value
     * @param firstAmount  The amount of steps for the first transition (start -> overflow)
     * @param secondAmount The amount of steps for the second transition (overflow -> end)
     * @param reachEnd     {@link #reachEnd}
     * @param reachStart   {@link #reachStart}
     */
    public OverflowDoubleTransition (final double start, final double overflow, final double end, final int firstAmount, final int secondAmount,
                                     final Runnable reachEnd, final Runnable reachStart, final IntSupplier autoTransformator)
    {
        super(reachEnd, reachStart, autoTransformator);

        synchronized (threadLock) {

            final boolean negative = end < start;

            Validate.isTrue(negative ? overflow < end : overflow > end, "The overflow value must be %s than the end value!", (negative ? "less" : "greater"));
            Validate.isTrue(start != overflow && start != end && end != overflow, "All of the values must be different!");

            this.first = DoubleTransition.builder().start(start).end(overflow).amountOfSteps(firstAmount).reachEnd(() -> currentlyFirst = false).reachStart(reachStart).build();
            this.second = DoubleTransition.builder().start(overflow).end(end).amountOfSteps(secondAmount).reachStart(() -> currentlyFirst = true).reachEnd(reachEnd).build();
        }
    }

    /**
     * The step-forward method for the transition.
     */
    @Override
    public void doForward ()
    {
        synchronized (threadLock) {
            if (currentlyFirst)
                first.setForward();
            else
                second.setForward();
        }
    }

    /**
     * The step-backward method for the transition.
     */
    @Override
    public void doBackward ()
    {
        synchronized (threadLock) {
            if (currentlyFirst)
                first.setBackward();
            else
                second.setBackward();
        }
    }

    /**
     * @return The current double value
     */
    @Override
    public double get ()
    {
        return currentlyFirst ? first.get() : second.get();
    }

    /**
     * @return The current double value casted to an integer
     */
    @Override
    public int castToInt ()
    {
        return ( int ) get();
    }

    /**
     * @return Whether the current value is at the end.
     */
    @Override
    public boolean isAtEnd ()
    {
        return !currentlyFirst && second.isAtEnd();
    }

    /**
     * @return Whether the current value is at the start.
     */
    @Override
    public boolean isAtStart ()
    {
        return currentlyFirst && first.isAtStart();
    }

    @Override
    public void destroy ()
    {
        first.destroy();
        second.destroy();

        super.destroy();
    }

    @Override
    public String toString ()
    {
        return "OverflowDoubleTransition{" +
               "originStackTrace=" + originStackTrace +
               '}';
    }

    public static OverflowDoubleTransitionBuilder builder ()
    {
        return new OverflowDoubleTransitionBuilder();
    }
}
