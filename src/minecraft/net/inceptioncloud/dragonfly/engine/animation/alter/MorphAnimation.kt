package net.inceptioncloud.dragonfly.engine.animation.alter

import net.inceptioncloud.dragonfly.engine.animation.Animation
import net.inceptioncloud.dragonfly.engine.internal.Widget
import net.inceptioncloud.dragonfly.engine.internal.annotations.Interpolate
import net.inceptioncloud.dragonfly.engine.sequence.Sequence
import kotlin.reflect.*
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties

/**
 * ## Morph Animation (Alter)
 *
 * A morph animation provides a smooth transition from one state (or instance) of a widget to another one.
 * It interpolates all dynamic properties and also modifies the base of the widget instead of only the scratchpad widget.
 *
 * @param destination the target state (or instance) of the widget
 * @param duration the amount of mod ticks (200 ^= 1s) that the animation should take to finish
 * @param easing an optional easing function
 */
class MorphAnimation(
    val destination: Widget<*>,
    val duration: Int = 100,
    val easing: ((Double) -> Double)? = null
) : Animation() {

    /**
     * Simple extension function to easily find a property by its name.
     */
    private fun KClass<*>.getPropertyByName(name: String): KProperty<*> = memberProperties.first { it.name == name }

    /**
     * Saves all dynamic properties of the parent widget with its corresponding sequences
     * that are used to interpolate them.
     */
    private val propertySequences: MutableMap<KMutableProperty<*>, Sequence<*>> = mutableMapOf()

    override fun initAnimation(parent: Widget<*>): Boolean {
        return if (super.initAnimation(parent)) {
            parent::class.memberProperties
                .filter { it.hasAnnotation<Interpolate>() && it is KMutableProperty<*> }
                .filter { it.getter.call(parent) != it.getter.call(destination) }
                .forEach {
                    val initialValue = it.getter.call(parent)
                    val destinationValue = destination::class.getPropertyByName(it.name).getter.call(destination)
                    val sequence = Sequence.generateSequence(initialValue, destinationValue, duration)
                        .withEasing(easing)

                    propertySequences[it as KMutableProperty<*>] = sequence
                }

            true
        } else false
    }

    override fun applyToShape(scratchpad: Widget<*>, base: Widget<*>) {
        for ((property, sequence) in propertySequences) {
            property.setter.call(base, sequence.current)
            property.setter.call(scratchpad, sequence.current)
        }

        if (propertySequences.values.any { it.isAtEnd }) {
            finish()
        }
    }

    override fun tick() {
        if (!running)
            return

        propertySequences.values.forEach { it.next() }
    }

    override fun isApplicable(widget: Widget<*>) = widget::class == destination::class

    /**
     * Defines only the [morph] function.
     */
    companion object {

        /**
         * A convenient function for morphing from one widget. This function alters the original widget
         * instead of accepting a destination widget for the animation.
         */
        fun <W : Widget<W>> Widget<W>.morph(
            duration: Int = 100,
            easing: ((Double) -> Double)? = null,
            alter: W.() -> Unit
        ): Animation? {
            val altered = altered(alter)

            if (isStateEqual(altered))
                return null

            return MorphAnimation(altered, duration, easing).also { attachAnimation(it) }
        }

        /**
         * A convenient function for morphing between multiple states of a widget.
         */
        fun <W : Widget<W>> Widget<W>.morphBetween(
            duration: Int = 100,
            easing: ((Double) -> Double)? = null,
            first: W.() -> Unit,
            second: W.() -> Unit
        ) {
            if (findAnimation<MorphAnimation>() != null)
                return

            val alteredFirst = altered(first)
            val alteredSecond = altered(second)
            val destination = if (isStateEqual(alteredSecond)) alteredFirst else alteredSecond

            attachAnimation(MorphAnimation(destination, duration, easing)) {
                start()
                post { _, widget -> widget.detachAnimation<MorphAnimation>() }
            }
        }
    }
}