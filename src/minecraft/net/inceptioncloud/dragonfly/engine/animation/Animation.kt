package net.inceptioncloud.dragonfly.engine.animation

import net.inceptioncloud.dragonfly.engine.internal.Widget
import net.inceptioncloud.dragonfly.transition.Transition

typealias PostAction = (Animation, Widget<*>) -> Unit

/**
 * ## Animation Class
 *
 * This class is the head of every animation. It provides several methods for handling animations
 * and some abstract methods that will be implemented in the child animations.
 */
abstract class Animation {
    /**
     * Represents if the animation has run through it's lifecycle and is now finished.
     *
     * If this flag is set to true, the animation will be removed from its parent elements
     * during the next [Widget.update] call, before the animation will be applied. It has a
     * private setter.
     */
    var finished = false
        private set

    /**
     * Whether the animation is currently running. By default, the flag is set to false but will be
     * updated to true once the [start] method was called.
     */
    var running = false

    /**
     * A list of actions that will be performed when the animation has finished.
     * These functions have the animation as a receiver so the variables can be
     * accessed easily.
     */
    val postActions = mutableListOf<PostAction>()

    /**
     * The parent of an animation is the widget object to which this animation applies. There can only
     * be one parent object for an animation, which means that an animation cannot be added to
     * multiple widgets.
     */
    lateinit var widget: Widget<*>

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
    open fun initAnimation(parent: Widget<*>): Boolean
    {
        return if (this::widget.isInitialized || !isApplicable(parent))
        {
            false
        } else
        {
            this.widget = parent
            true
        }
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

        running = true

        return this
    }

    /**
     * Finishes the animation.
     *
     * This sets the [finished] flag to true, what will lead the [Widget] to remove the animation from the
     * [animation stack][Widget.animationStack] during the next call of the [update][Widget.update] function.
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
        postActions.forEach { it.invoke(this, widget) }
    }

    /**
     * Applies the animation to the widget base.
     *
     * Do this by changing the values of the [scratchpad] parameter. This is a clone of the base
     * widget to which the changes can be made. For manipulating values relative to a base value,
     * the [base] parameter is passed.
     *
     * @param scratchpad the version of the widget that should be modified
     * @param base a version of the widget that holds initial values
     */
    abstract fun applyToShape(scratchpad: Widget<*>, base: Widget<*>)

    /**
     * Checks if the animation is available for the given widget type.
     */
    abstract fun isApplicable(widget: Widget<*>): Boolean

    /**
     * Performs a tick on the animation.
     *
     * This is called from the [Widget.update] function. If the animation uses one or more
     * [transitions][Transition], the [Transition.directedUpdate] method should be called here.
     */
    abstract fun tick()
}

/**
 * Convenient method for adding a post action to the animation and returning the instance.
 */
fun Animation.post(action: PostAction) = this.apply { postActions.add(action) }