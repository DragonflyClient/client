package net.inceptioncloud.minecraftmod.transition.number;

import lombok.Builder;
import net.inceptioncloud.minecraftmod.transition.FloatingDirection;

/**
 * <h2>Floating Double Transition</h2>
 * <p>
 * Supplies a double value that transforms from the start value to the end value and from the end value to the start value
 * in both directions.
 *
 * <b>Note:</b> This type of transition does not differentiate between {@link #setForward()} and {@link #setBackward()}.
 */
public class FloatingDoubleTransition extends TransitionTypeNumber
{
    /**
     * The double transition base.
     */
    private final DoubleTransition base;

    /**
     * The current floating direction.
     */
    @Builder.Default
    private FloatingDirection direction = FloatingDirection.FORWARD;

    /**
     * Create a new floating double transition instance.
     *
     * @see DoubleTransition Parameter Documentation
     */
    @Builder
    private FloatingDoubleTransition (final double start, final double end, final int amountOfSteps, final Runnable reachEnd, final Runnable autoTransformator)
    {
        super(null, null, null);
        this.base = DoubleTransition.builder().start(start).end(end).amountOfSteps(amountOfSteps).reachEnd(reachEnd).reachStart(autoTransformator).build();
    }

    /**
     * Performs either {@link #setForward()} or {@link #setBackward()} based on the {@link #direction}.
     */
    public void next ()
    {
        if (direction == FloatingDirection.FORWARD) {
            this.base.setForward();

            if (isAtEnd())
                direction = FloatingDirection.BACKWARD;
        } else {
            this.base.setBackward();

            if (isAtStart())
                direction = FloatingDirection.FORWARD;
        }
    }

    /**
     * @return The current double value
     */
    @Override
    public double get ()
    {
        return this.base.get();
    }

    /**
     * @return The current double value casted to an integer.
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
        return this.base.isAtEnd();
    }

    /**
     * @return Whether the current value is at the start.
     */
    @Override
    public boolean isAtStart ()
    {
        return this.base.isAtStart();
    }

    /**
     * The step-forward method for the transition.
     */
    @Override
    public void doForward ()
    {
        this.next();
    }

    /**
     * The step-backward method for the transition.
     */
    @Override
    public void doBackward ()
    {
        this.next();
    }

    @Override
    public void destroy ()
    {
        base.destroy();
        super.destroy();
    }

    @Override
    public String toString ()
    {
        return "FloatingDoubleTransition{" +
               "originStackTrace=" + originStackTrace +
               '}';
    }
}
