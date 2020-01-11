package net.inceptioncloud.minecraftmod.transition.string;

import lombok.Builder;
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
     * The content of the string transition.
     */
    @Builder.Default
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
    @Builder
    public SubstringTransition (final String content, final int amountOfSteps, final Runnable reachEnd, final Runnable reachStart, final IntSupplier autoTransformator)
    {
        super(reachEnd, reachStart, autoTransformator);

        Validate.notNull(content, "The content cannot be null!");

        this.content = content;
        this.base = DoubleTransition.builder()
            .start(0)
            .end(this.content.length())
            .amountOfSteps(amountOfSteps)
            .build();
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
        base.setForward();
    }

    /**
     * The step-backward method for the transition.
     */
    @Override
    public void doBackward ()
    {
        base.setBackward();
    }
}
