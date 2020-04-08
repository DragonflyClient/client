package net.inceptioncloud.minecraftmod.transition.color;

import lombok.Builder;
import net.inceptioncloud.minecraftmod.transition.FloatingDirection;

import java.awt.*;
import java.util.function.IntSupplier;

/**
 * <h2>Floating Color Transition</h2>
 * <p>
 * Supplies a color value that transforms from the start value to the end value and from the end value to the start value
 * in both directions.
 *
 * <b>Note:</b> This type of transition does not differentiate between {@link #setForward()} and {@link #setBackward()}.
 */
public class FloatingColorTransition extends TransitionTypeColor
{
    /**
     * The color transition base.
     */
    private final ColorTransition base;

    /**
     * The current floating direction.
     */
    @Builder.Default
    private FloatingDirection direction = FloatingDirection.FORWARD;

    /**
     * Create a new floating color transition instance.
     *
     * @see ColorTransition Parameter Documentation
     */
    @Builder
    public FloatingColorTransition (final Color start, final Color end, final int amountOfSteps,
                                    final Runnable reachEnd, final Runnable reachStart, final IntSupplier autoTransformator)
    {
        super(null, null, autoTransformator);
        this.base = ColorTransition.builder().start(start).end(end).amountOfSteps(amountOfSteps).reachStart(reachStart).reachEnd(reachEnd).build();
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
     * @return The current color value
     */
    @Override
    public Color get ()
    {
        return this.base.get();
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
        return "FloatingColorTransition{" +
               "originStackTrace=" + originStackTrace +
               '}';
    }
}
