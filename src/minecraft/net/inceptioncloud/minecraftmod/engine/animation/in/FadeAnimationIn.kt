package net.inceptioncloud.minecraftmod.engine.animation.`in`

import net.inceptioncloud.minecraftmod.engine.animation.Animation
import net.inceptioncloud.minecraftmod.engine.internal.Shape2D
import net.inceptioncloud.minecraftmod.transition.Transition
import net.inceptioncloud.minecraftmod.transition.number.DoubleTransition
import net.inceptioncloud.minecraftmod.transition.number.SmoothDoubleTransition
import net.inceptioncloud.minecraftmod.transition.number.TransitionTypeNumber

/**
 * ## Fade Animation (In)
 *
 * A simple animation that fades the parent object in by increasing the opacity stepwise.
 *
 * @param smooth decides whether a [SmoothDoubleTransition] or a default [DoubleTransition] should be used
 */
open class FadeAnimationIn(val smooth: Boolean) : Animation()
{
    /**
     * A simple [Transition] that provides the opacity of the object.
     *
     * This can either be a [SmoothDoubleTransition] or a [DoubleTransition] depending
     * on the smooth flag in the constructor of the animation.
     */
    private lateinit var transition: TransitionTypeNumber

    /**
     * The start value of the transition.
     *
     * This can be modified to create a reversed (out) animation by simply extending this
     * class and overriding this and the [endValue] value.
     */
    open val startValue = 0.0

    /**
     * The end value of the transition.
     *
     * @see startValue
     */
    open val endValue = 1.0

    /**
     * Initializes the animation.
     *
     * This function runs the required code to initialize (prepare) the animation in the specific
     * subclass. It is for instance used to create transitions.
     */
    override fun initAnimation(parent: Shape2D<*>)
    {
        super.initAnimation(parent)

        val fadeIn = 20
        val stay = 60
        val fadeOut = 20

        transition = if (smooth)
        {
            SmoothDoubleTransition.builder()
                .start(startValue)
                .end(endValue)
                .fadeIn(fadeIn)
                .stay(stay)
                .fadeOut(fadeOut)
                .reachEnd { finish() }
                .build()
        } else
        {
            DoubleTransition.builder()
                .start(startValue)
                .end(endValue)
                .amountOfSteps(fadeIn + stay + fadeOut)
                .reachEnd { finish() }
                .build()
        }
    }

    /**
     * Applies the animation to the shape base.
     *
     * Do this by changing the values of the [scratchpad] parameter. This is a clone of the base
     * shape to which the changes can be made. For manipulating values relative to a base value,
     * the [base] parameter is passed.
     *
     * @param scratchpad the version of the shape that should be modified
     * @param base a version of the shape that holds base values set by the [Shape2D.static] or
     * [Shape2D.dynamic] function
     */
    override fun applyToShape(scratchpad: Shape2D<*>, base: Shape2D<*>)
    {
        scratchpad.color.alphaDouble = transition.get() * base.color.alphaDouble
    }

    /**
     * Performs a tick on the animation.
     *
     * This is called from the [Shape2D.update] function. If the animation uses one or more
     * [transitions][Transition], the [Transition.directedUpdate] method should be called here.
     */
    override fun tick()
    {
        transition.directedUpdate()
    }

    /**
     * Starts the animation.
     *
     * Before this method is called, the animation shouldn't use any transitions. It is used to support
     * fade-in transitions that start after a given time, but the object should already be hidden before.
     * When using one or more [transitions][Transition], this should call [Transition.setForward].
     */
    override fun start(): Animation
    {
        // state-check
        super.start()

        transition.setForward()
        return this
    }

}