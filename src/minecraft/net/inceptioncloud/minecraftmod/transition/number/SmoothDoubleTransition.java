package net.inceptioncloud.minecraftmod.transition.number;

import lombok.Getter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntSupplier;

/**
 * <h2>Double Transition</h2>
 * <p>
 * The transition that supplies a double value that transforms from the start value to the end value in only that direction.
 */
@Getter
public class SmoothDoubleTransition extends TransitionTypeNumber
{
    /**
     * A simple object that the constructor and non-thread-safe methods are synchronized on.
     * The content of this object is never used and it is never updated or accessed.
     *
     * @since v1.0.1.0 ~ hotfix/transition-thread-safe
     */
    private final Object threadLock = new Object();

    /**
     * The transition that manages the fading in process. Calculates the distance per step during the fade-in.
     */
    private final DoubleTransition fadeInTransition;

    /**
     * The transition that manages the fading out process. Calculates the distance per step during the fade-out.
     */
    private final DoubleTransition fadeOutTransition;

    /**
     * The amount the current value is changed with when processing a during the stay phase.
     */
    private final double perStepStay;

    /**
     * The start value.
     */
    protected double start;

    /**
     * The end value.
     */
    protected double end;

    /**
     * The average amount the current value is changed with when processing a step.
     */
    protected double perStep;

    /**
     * Whether the transition goes from positive to negative values.
     */
    protected boolean negative;

    /**
     * The current double value.
     */
    protected double current;

    /**
     * The amount of steps with which the animation staying constant.
     */
    protected int stay;

    /**
     * The amount of steps with which the animation is fading in.
     */
    protected int fadeIn;

    /**
     * The amount of steps with which the animation is fading out.
     */
    protected int fadeOut;

    /**
     * The amount of steps to take from the start to the end.
     */
    protected int amountOfSteps;

    /**
     * The currently taken step.
     */
    private int currentStep = 1;

    /**
     * The last time the value was transformed.
     */
    private long lastTransform;

    /**
     * The amount of distance that the fadeOut phase can proceed.
     */
    private double fadeOutDistance = 0;

    /**
     * The amount of distance that the fadeIn phase can proceed.
     */
    private double fadeInDistance = 0;

    /**
     * Create a new double transition instance.
     *
     * @param start      {@link #start}
     * @param end        {@link #end}
     * @param reachEnd   {@link #reachEnd}
     * @param reachStart {@link #reachStart}
     */
    SmoothDoubleTransition (final double start, final double end, final int fadeIn, final int stay, final int fadeOut
        , final Runnable reachEnd, final Runnable reachStart, final IntSupplier autoTransformator)
    {
        super(reachEnd, reachStart, autoTransformator);

        synchronized (threadLock) {

            this.negative = start > end;
            this.start = start;
            this.end = end;
            this.current = start;

            this.fadeIn = fadeIn;
            this.fadeOut = fadeOut;
            this.stay = stay;

            amountOfSteps = fadeIn + stay + fadeOut;
            perStep = (Math.max(start, end) - Math.min(start, end)) / amountOfSteps;

            fadeInTransition = DoubleTransition.builder().start(0).end(perStep).amountOfSteps(fadeIn).build();
            fadeOutTransition = DoubleTransition.builder().start(perStep).end(0).amountOfSteps(fadeOut).build();

            Function<Double, Double> fadeOutFunction = x -> perStep - (x * (perStep / fadeOut));
            Function<Double, Double> fadeInFunction = x -> perStep - (x * (perStep / fadeIn));

            for (int x = 1 ; x <= fadeOut ; x++)
                fadeOutDistance += fadeOutFunction.apply((double) x);

            for (int x = 1 ; x <= fadeIn ; x++)
                fadeInDistance += fadeInFunction.apply((double) x);

            double stayStart = start + (negative ? -fadeInDistance : fadeInDistance);
            double stayEnd = end - (negative ? -fadeOutDistance : fadeOutDistance);
            perStepStay = (Math.max(stayStart, stayEnd) - Math.min(stayStart, stayEnd)) / stay;
        }
    }

    public static SmoothDoubleTransitionBuilder builder ()
    {
        return new SmoothDoubleTransitionBuilder();
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
                current -= getPhasePerStep();
            else
                current += getPhasePerStep();

            lastTransform = System.currentTimeMillis();

            getPhaseTransition().ifPresent(DoubleTransition::doForward);

            // If the value is at the end
            if (isAtEnd()) {
                current = end;

                // Call the runnable
                if (reachEnd != null) reachEnd.run();
            } else currentStep++;

            keepInBounds();
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
                current += getPhasePerStep();
            else
                current -= getPhasePerStep();

            lastTransform = System.currentTimeMillis();

            getPhaseTransition().ifPresent(DoubleTransition::doBackward);

            // If the value is at the start
            if (isAtStart()) {
                current = start;

                // Call the runnable
                if (reachStart != null) reachStart.run();
            } else currentStep--;

            keepInBounds();
        }
    }

    /**
     * @return Whether the current value is at the end.
     */
    @Override
    public boolean isAtEnd ()
    {
        return getPhase() == 3 && getPhasePerStep() == 0.0D;
//        return negative ? getDistance() - (getDistance() / 100) <= end : getValidatedValue() + (getDistance() / 100) >= end;
    }

    /**
     * @return Whether the current value is at the start.
     */
    @Override
    public boolean isAtStart ()
    {
        return getPhase() == 1 && getPhasePerStep() == 0.0D;
//        return negative ? current + (getDistance() / 100)>= start : current - (getDistance() / 100) <= start;
    }

    /**
     * @return The current double value
     */
    @Override
    public double get ()
    {
        keepInBounds();
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

    /**
     * Makes sure the value doesn't run out of bounds.
     */
    public void keepInBounds ()
    {
        if (negative)
            current = Math.min(start, Math.max(current, end));
        else
            current = Math.max(start, Math.min(current, end));
    }

    /**
     * Returns in which phase the transition is currently.
     * <p>
     * 1 - fadeIn<br/> 2 - stay<br/> 3 - fadeOut<br/>
     */
    public int getPhase ()
    {
        if (currentStep < fadeIn)
            return 1;
        else if (currentStep >= fadeIn + stay)
            return 3;
        else return 2;
    }

    /**
     * Calculates the current distance a step should take.
     */
    public synchronized double getPhasePerStep ()
    {
        try {
            int phase = getPhase();
            return
                phase == 1 ? Objects.requireNonNull(fadeInTransition).get()
                : phase == 3 ? Objects.requireNonNull(fadeOutTransition).get()
                : perStepStay;
        } catch (NullPointerException exception) {
            exception.printStackTrace();
            System.out.println(getOriginStackTrace());

            return 0;
        }
    }

    /**
     * Returns the transition that is responsible for the current phase of the animation.
     */
    public Optional<DoubleTransition> getPhaseTransition ()
    {
        int phase = getPhase();
        return phase == 1 ? Optional.of(fadeInTransition) :
            phase == 3 ? Optional.of(fadeOutTransition) : Optional.empty();
    }

    @Override
    public String toString ()
    {
        return "SmoothDoubleTransition{" +
               "originStackTrace=" + originStackTrace +
               '}';
    }

    /**
     * better readable toString()
     */
    public String getState ()
    {
        NumberFormat format = new DecimalFormat("000.00");
        return "SmoothDoubleTransition -> val: " + format.format(current) + " (" + currentStep + "/" + amountOfSteps +
               ") during phase " + getPhase() + " with " + format.format(getPhasePerStep()) + " {fod=" +
               fadeOutDistance + "}";
    }

    @Override
    public void destroy ()
    {
        fadeInTransition.destroy();
        fadeOutTransition.destroy();

        super.destroy();
    }
}
