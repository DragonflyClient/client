package net.inceptioncloud.minecraftmod.transition.string;

import net.inceptioncloud.minecraftmod.transition.number.DoubleTransition;
import org.apache.commons.lang3.Validate;

import java.util.function.IntSupplier;

/**
 * <h2>Substring Transition</h2>
 * <p>
 * The transition that builds up a string letter by letter.
 */
public class SubstringTransition extends TransitionTypeString
{
    /**
     * A simple object that the constructor and non-thread-safe methods are synchronized on.
     * The content of this object is never used and it is never updated or accessed.
     *
     * @since v1.0.1.0 ~ hotfix/transition-thread-safe
     */
    private final Object threadLock = new Object();

    /**
     * The content of the string transition.
     */
    protected String content = "This is a string transition.";

    /**
     * The base double transition.
     */
    protected DoubleTransition base;

    /**
     * Create a new substring transition instance.
     *
     * @param content              {@link #content}
     * @param amountOfSteps        The amount of steps for the {@link #base}
     * @param reachEnd             {@link #reachEnd}
     * @param reachStart           {@link #reachStart}
     * @param autoTransformator {@link #autoTransformator}
     */
    public SubstringTransition (final String content, final int amountOfSteps, final Runnable reachEnd, final Runnable reachStart, final IntSupplier autoTransformator)
    {
        super(reachEnd, reachStart, autoTransformator);

        synchronized (threadLock) {

            Validate.notNull(content, "The content cannot be null!");

            this.content = content;
            this.base = DoubleTransition.builder()
                .start(0)
                .end(this.content.length())
                .amountOfSteps(amountOfSteps)
                .build();
        }
    }

    /**
     * @return The current value.
     */
    @Override
    public String get ()
    {
        return content.substring(0, base.castToInt());
    }

    /**
     * @return Whether the current value is at the end.
     */
    @Override
    public boolean isAtEnd ()
    {
        return base.isAtEnd();
    }

    /**
     * @return Whether the current value is at the start.
     */
    @Override
    public boolean isAtStart ()
    {
        return base.isAtStart();
    }

    /**
     * The step-forward method for the transition.
     */
    @Override
    public void doForward ()
    {
        synchronized (threadLock) {
            base.setForward();
        }
    }

    /**
     * The step-backward method for the transition.
     */
    @Override
    public void doBackward ()
    {
        synchronized (threadLock) {
            base.setBackward();
        }
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
        return "SubstringTransition{" +
               "originStackTrace=" + originStackTrace +
               '}';
    }

    public static SubstringTransitionBuilder builder ()
    {
        return new SubstringTransitionBuilder();
    }
}
