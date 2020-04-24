package net.inceptioncloud.minecraftmod.engine.animation

import net.inceptioncloud.minecraftmod.engine.internal.Shape2D
import net.inceptioncloud.minecraftmod.transition.Transition

/**
 * ## Animation Class
 *
 * This class is the head of every animation. It provides several methods for handling animations
 * and some abstract methods that will be implemented in the child animations.
 */
abstract class Animation
{
    /**
     * Represents if the animation has run through it's lifecycle and is now finished.
     *
     * If this flag is set to true, the animation will be removed from its parent elements
     * during the next [Shape2D.update] call, before the animation will be applied. It has a
     * private setter.
     */
    var finished = false
        private set

    /**
     * The parent of an animation is the shape object to which this animation applies. There can only
     * be one parent object for an animation, which means that an animation cannot be added to
     * multiple shapes.
     */
    lateinit var parent: Shape2D<*>

    /**
     * Initializes the animation.
     *
     * **NOTE:** Make sure to keep the super-call as the first statement when overriding the function.
     *
     * This function assigns the parent element variable and runs the required code to initialize (prepare)
     * the animation in the specific subclass. It is for instance used to create transitions.
     *
     * @throws IllegalArgumentException if the animation is already bound to a parent element
     */
    open fun initAnimation(parent: Shape2D<*>)
    {
        if (this::parent.isInitialized)
        {
            throw IllegalStateException("Animation is already bound to a parent element!")
        } else this.parent = parent
    }

    /**
     * Starts the animation.
     *
     * **NOTE:** When overriding this method, the super-call should be the first statement as it validates the
     * use of the method!
     *
     * Before this method is called, the animation shouldn't use any transitions. It is used to support
     * fade-in transitions that start after a given time, but the object should already be hidden before.
     * When using one or more [transitions][Transition], this should call [Transition.setForward].
     *
     * @throws IllegalStateException if the animation has already finished
     */
    open fun start(): Animation
    {
        if (finished)
        {
            throw IllegalStateException("The animation has already finished and cannot be started again!")
        }

        return this
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
    abstract fun applyToShape(scratchpad: Shape2D<*>, base: Shape2D<*>)

    /**
     * Performs a tick on the animation.
     *
     * This is called from the [Shape2D.update] function. If the animation uses one or more
     * [transitions][Transition], the [Transition.directedUpdate] method should be called here.
     */
    abstract fun tick()

    /**
     * Finishes the animation.
     *
     * This sets the [finished] flag to true, what will lead the [Shape2D] to remove the animation from the
     * [animation stack][Shape2D.animationStack] during the next call of the [update][Shape2D.update] function.
     * It should be called when the animation was ran through its lifecycle what is mostly the case when all
     * [transitions][Transition] have reached their end. Therefore it makes sense to use the animation builder
     * and add a reach-end-hook.
     * ```kotlin
     * SmoothDoubleTransition.builder().reachEnd { finish() }.build()
     * ```
     */
    protected open fun finish()
    {
        finished = true
    }
}