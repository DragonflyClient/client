package net.inceptioncloud.minecraftmod.transition.number;

import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntSupplier;

/**
 * <h2>Double Transition</h2>
 * <p>
 * The transition that supplies a double value that transforms from the start value to the end value
 * in only that direction.
 */
@Getter
public class SmoothDoubleTransition extends TransitionTypeNumber
{
    /**
     * The transition that manages the fading in process.
     * Calculates the distance per step during the fade-in.
     */
    private final DoubleTransition fadeInTransition;

    /**
     * The transition that manages the fading out process.
     * Calculates the distance per step during the fade-out.
     */
    private final DoubleTransition fadeOutTransition;

    /**
     * The start value.
     */
    @Builder.Default
    protected double start = 10;

    /**
     * The end value.
     */
    @Builder.Default
    protected double end = 100;

    /**
     * The average amount the current value is changed with when processing a step.
     */
    @Builder.Default
    protected double perStep = 10;

    /**
     * The amount the current value is changed with when processing a during the stay phase.
     */
    private final double perStepStay;

    /**
     * Whether the transition goes from positive to negative values.
     */
    @Builder.Default
    protected boolean negative = false;

    /**
     * The current double value.
     */
    @Builder.Default
    protected double current = 0;

    /**
     * The amount of steps with which the animation staying constant.
     */
    @Builder.Default
    protected int stay = 40;

    /**
     * The amount of steps with which the animation is fading in.
     */
    @Builder.Default
    protected int fadeIn = 20;

    /**
     * The amount of steps with which the animation is fading out.
     */
    @Builder.Default
    protected int fadeOut = 20;

    /**
     * The amount of steps to take from the start to the end.
     */
    @Getter
    protected int amountOfSteps;

    /**
     * The currently taken step.
     */
    @Getter
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
    @Builder ( toBuilder = true )
    private SmoothDoubleTransition (final double start, final double end, final int fadeIn, final int stay, final int fadeOut, final Runnable reachEnd, final Runnable reachStart, final IntSupplier autoTransformator)
    {
        super(reachEnd, reachStart, autoTransformator);

        this.negative = start > end; // Check whether the double transition value is getting less when processing a forward step
        this.start = start; // Pass the start value
        this.end = end; // Pass the end value
        this.current = start; // Set the current value to the start value

        this.fadeIn = fadeIn;
        this.fadeOut = fadeOut;
        this.stay = stay;

        this.amountOfSteps = fadeIn + stay + fadeOut;
        this.perStep = ( Math.max(start, end) - Math.min(start, end) ) / amountOfSteps; // Calculate the value with which the current value is modified when processing a step

        this.fadeInTransition = DoubleTransition.builder().start(0).end(perStep).amountOfSteps(fadeIn).build();
        this.fadeOutTransition = DoubleTransition.builder().start(perStep).end(0).amountOfSteps(fadeOut).build();

        Function<Double, Double> fadeOutFunction = x -> perStep - ( x * ( perStep / fadeOut ) );
        Function<Double, Double> fadeInFunction = x -> perStep - ( x * ( perStep / fadeIn ) );

        for (int x = 1 ; x <= fadeOut ; x++)
            fadeOutDistance += fadeOutFunction.apply(( double ) x);

        for (int x = 1 ; x <= fadeIn ; x++)
            fadeInDistance += fadeInFunction.apply(( double ) x);

        double stayStart = start + ( negative ? -fadeInDistance : fadeInDistance );
        double stayEnd = end - ( negative ? -fadeOutDistance : fadeOutDistance );
        perStepStay = ( Math.max(stayStart, stayEnd) - Math.min(stayStart, stayEnd) ) / stay;
    }

    /**
     * The step-forward method for the double transition.
     */
    @Override
    public void doForward ()
    {
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
        keepInBounds();

        // If the value is at the end
        if (isAtEnd()) {
            current = end;

            // Call the runnable
            if (reachEnd != null) reachEnd.run();
        } else currentStep++;
    }

    /**
     * The step-backward method for the double transition.
     */
    @Override
    public void doBackward ()
    {
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
        keepInBounds();

        // If the value is at the start
        if (isAtStart()) {
            current = start;

            // Call the runnable
            if (reachStart != null) reachStart.run();
        } else currentStep--;
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
        return current;
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
     * Change the end value
     *
     * @param end The end value
     */
    public void setEnd (final double end)
    {
        this.end = end;
        this.perStep = ( Math.max(start, end) - Math.min(start, end) ) / amountOfSteps; // Calculate the value with which the current value is modified when processing a step
    }

    /**
     * Makes sure the value doesn't run out of bounds.
     */
    public void keepInBounds()
    {
        if (negative)
            current = Math.min(start, Math.max(current, end));
        else
            current = Math.max(start, Math.min(current, end));
    }

    /**
     * Returns in which phase the transition is currently.
     * <p>
     * 1 - fadeIn<br/>
     * 2 - stay<br/>
     * 3 - fadeOut<br/>
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
    public double getPhasePerStep ()
    {
        int phase = getPhase();
        return phase == 1 ? fadeInTransition.get() : phase == 3 ? fadeOutTransition.get() : perStepStay;
    }

    /**
     * Returns the transition that is responsible for the current phase of the animation.
     */
    public Optional<DoubleTransition> getPhaseTransition ()
    {
        int phase = getPhase();
        return phase == 1 ? Optional.of(fadeInTransition) : phase == 3 ? Optional.of(fadeOutTransition) : Optional.empty();
    }

    /**
     * toString() support
     */
    @Override
    public String toString ()
    {
        return "SmoothDoubleTransition{" +
               "start=" + start +
               ", end=" + end +
               ", negative=" + negative +
               ", stay=" + stay +
               ", fadeIn=" + fadeIn +
               ", fadeOut=" + fadeOut +
               ", autoTransformator=" + autoTransformator +
               '}';
    }

    /**
     * better readable toString()
     */
    public String getState ()
    {
        NumberFormat format = new DecimalFormat("000.00");
        return "SmoothDoubleTransition -> val: " + format.format(current) + " (" + currentStep + "/" + amountOfSteps + ") during phase " + getPhase() + " with " + format.format(getPhasePerStep()) + " {fod=" + fadeOutDistance + "}";
    }
}
