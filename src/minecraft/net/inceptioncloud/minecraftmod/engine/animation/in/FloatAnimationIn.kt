package net.inceptioncloud.minecraftmod.engine.animation.`in`

import net.inceptioncloud.minecraftmod.engine.animation.Animation
import net.inceptioncloud.minecraftmod.engine.internal.Shape2D
import net.inceptioncloud.minecraftmod.transition.Transition

class FloatAnimationIn : Animation()
{
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
        TODO("Not yet implemented")
    }

    /**
     * Performs a tick on the animation.
     *
     * This is called from the [Shape2D.update] function. If the animation uses one or more
     * [transitions][Transition], the [Transition.directedUpdate] method should be called here.
     */
    override fun tick()
    {
        TODO("Not yet implemented")
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
    override fun start(): Animation
    {
        super.start()

        return this
    }
}