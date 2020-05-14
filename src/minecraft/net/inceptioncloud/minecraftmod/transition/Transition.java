package net.inceptioncloud.minecraftmod.transition;

import net.inceptioncloud.minecraftmod.Dragonfly;
import net.inceptioncloud.minecraftmod.transition.color.*;
import net.inceptioncloud.minecraftmod.transition.number.*;
import net.inceptioncloud.minecraftmod.transition.string.*;
import net.inceptioncloud.minecraftmod.ui.components.button.*;
import net.inceptioncloud.minecraftmod.utils.RuntimeUtils;
import net.minecraft.client.gui.GuiButton;

import java.util.Objects;
import java.util.function.IntSupplier;

/**
 * <h2>Transition</h2>
 * <p>
 * The transition superclass represents any transition.
 * It can be a {@link TransitionTypeNumber number}-, {@link TransitionTypeString string}- or {@link TransitionTypeColor color}-transition.
 */
public abstract class Transition
{
    /**
     * The runnable whose {@link Runnable#run()} method is invoked when the transition reaches it's end.
     */
    protected final Runnable reachEnd;

    /**
     * The runnable whose {@link Runnable#run()} method is invoked when the transition reaches it's start.
     */
    protected final Runnable reachStart;

    /**
     * If this value is not null, the transition will automatically transform forward if the supplied value
     * is positive, and backward if the supplied value is negative. If the value is 0, this will do nothing.
     */
    protected final IntSupplier autoTransformator;

    /**
     * The origin of the transition (the class that it was created in).
     */
    protected final String originClass;

    /**
     * Stack Trace of the origin.
     */
    protected final StackTraceElement originStackTrace;

    /**
     * The currently set direction.
     * <p>
     * 1 = forward
     * 0 = nothing
     * -1 = backward
     */
    protected int direction = 0;

    /**
     * The default transition constructor.
     */
    public Transition (final Runnable reachEnd, final Runnable reachStart, final IntSupplier autoTransformator)
    {
        this.reachEnd = reachEnd;
        this.reachStart = reachStart;
        this.autoTransformator = autoTransformator;

        final StackTraceElement stackTrace = RuntimeUtils.getStackTrace(Transition.class,
            TransitionTypeNumber.class,
            TransitionTypeColor.class,
            TransitionTypeString.class,
            DoubleTransition.class,
            ColorTransition.class,
            SubstringTransition.class,
            FloatingDoubleTransition.class,
            FloatingColorTransition.class,
            OverflowDoubleTransition.class,
            SmoothDoubleTransition.class,
            DoubleTransitionBuilder.class,
            ColorTransitionBuilder.class,
            SubstringTransitionBuilder.class,
            FloatingDoubleTransitionBuilder.class,
            FloatingColorTransitionBuilder.class,
            OverflowDoubleTransitionBuilder.class,
            SmoothDoubleTransitionBuilder.class,

            TransparentButton.class,
            SimpleButton.class,
            GuiButton.class,
            ConfirmationButton.class
        );

        String[] split = Objects.requireNonNull(stackTrace).getClassName().split("\\.");
        originClass = split[split.length - 1];
        originStackTrace = stackTrace;

        Dragonfly.handleTransition(this);
    }

    /**
     * Set the direction to forward.
     */
    public void setForward ()
    {
        direction = 1;
    }

    /**
     * Set the direction to backward.
     */
    public void setBackward ()
    {
        direction = -1;
    }

    /**
     * Set the direction to nothing.
     */
    public void setNothing ()
    {
        direction = 0;
    }

    /**
     * The default tick method.
     */
    public final void tick ()
    {
        if (autoTransformator != null) {
            int direction = autoTransformator.getAsInt();

            switch (direction) {
                case 1:
                    doForward();
                    break;

                case -1:
                    doBackward();
                    break;

                default:
                    break;
            }
        } else if (!originStackTrace.getClassName().contains("Animation")) {
            directedUpdate();
        }
    }

    /**
     * The default tick method.
     */
    public final void directedUpdate ()
    {
        int direction = getDirection();

        switch (direction) {
            case 1:
                doForward();
                break;

            case -1:
                doBackward();
                break;

            default:
                break;
        }
    }

    /**
     * Destroys the transition.
     */
    public void destroy ()
    {
        Dragonfly.stopTransition(this);
    }

    /**
     * Does the forward transition.
     */
    protected abstract void doForward ();

    /**
     * Does the backward transition.
     */
    protected abstract void doBackward ();

    /**
     * @return Whether the current value is at the end.
     */
    public abstract boolean isAtEnd ();

    /**
     * @return Whether the current value is at the start.
     */
    public abstract boolean isAtStart ();

    @Override
    public String toString ()
    {
        return "Transition{" +
               "originStackTrace=" + originStackTrace +
               '}';
    }

    public Runnable getReachEnd ()
    {
        return reachEnd;
    }

    public Runnable getReachStart ()
    {
        return reachStart;
    }

    public IntSupplier getAutoTransformator ()
    {
        return autoTransformator;
    }

    public String getOriginClass ()
    {
        return originClass;
    }

    public StackTraceElement getOriginStackTrace ()
    {
        return originStackTrace;
    }

    public int getDirection ()
    {
        return direction;
    }
}
