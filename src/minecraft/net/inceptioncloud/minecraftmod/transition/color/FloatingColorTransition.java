package net.inceptioncloud.minecraftmod.transition.color;

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
     * A simple object that the constructor and non-thread-safe methods are synchronized on.
     * The content of this object is never used and it is never updated or accessed.
     *
     * @since v1.0.1.0 ~ hotfix/transition-thread-safe
     */
    private final Object threadLock = new Object();

    /**
     * The color transition base.
     */
    private final ColorTransition base;

    /**
     * The current floating direction.
     */
    private FloatingDirection direction = FloatingDirection.FORWARD;

    /**
     * Create a new floating color transition instance.
     *
     * @see ColorTransition Parameter Documentation
     */
    public FloatingColorTransition (final Color start, final Color end, final int amountOfSteps,
                                    final Runnable reachEnd, final Runnable reachStart, final IntSupplier autoTransformator)
    {
        super(null, null, autoTransformator);

        synchronized (threadLock) {
            this.base = new ColorTransitionBuilder().start(start).end(end).amountOfSteps(amountOfSteps).reachStart(reachStart).reachEnd(reachEnd).build();
        }
    }

    public static FloatingColorTransitionBuilder builder ()
    {
        return new FloatingColorTransitionBuilder();
    }

    /**
     * Performs either {@link #setForward()} or {@link #setBackward()} based on the {@link #direction}.
     */
    public void next ()
    {
        synchronized (threadLock) {
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
