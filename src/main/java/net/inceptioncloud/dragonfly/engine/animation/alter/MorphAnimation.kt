package net.inceptioncloud.dragonfly.engine.animation.alter

import net.inceptioncloud.dragonfly.engine.animation.Animation
import net.inceptioncloud.dragonfly.engine.internal.Widget
import net.inceptioncloud.dragonfly.engine.internal.annotations.Interpolate
import net.inceptioncloud.dragonfly.engine.sequence.Sequence
import org.apache.logging.log4j.LogManager
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties

typealias PropertyUpdate = Pair<KProperty<*>, Any?>

/**
 * ## Morph Animation (Alter)
 *
 * A morph animation provides a smooth transition from one state (or instance) of a widget to another one.
 * It interpolates all dynamic properties and also modifies the base of the widget instead of only the scratchpad widget.
 *
 * @param updates the property updates that lead to the target state of the widget
 * @param duration the amount of mod ticks (200 ^= 1s) that the animation should take to finish
 * @param easing an optional easing function
 */
class MorphAnimation(
    val updates: List<PropertyUpdate>,
    val duration: Int = 100,
    val easing: ((Double) -> Double)? = null
) : Animation() {

    /**
     * Saves all dynamic properties of the parent widget with its corresponding sequences
     * that are used to interpolate them.
     */
    private val propertySequences: MutableMap<KMutableProperty<*>, Sequence<*>> = mutableMapOf()

    override fun initAnimation(parent: Widget<*>): Boolean {
        return if (super.initAnimation(parent)) {
            updates.forEach { (generalProp, destination) ->
                val prop = getPropertyIn(generalProp, parent)
                val initialValue = prop.getter.call(parent)
                val sequence = Sequence.generateSequence(initialValue, destination, duration)
                    .withEasing(easing)

                propertySequences[prop as KMutableProperty<*>] = sequence
            }

            true
        } else false
    }

    override fun applyToShape(base: Widget<*>) {
        for ((property, sequence) in propertySequences) {
            property.setter.call(base, sequence.current)
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

    override fun isApplicable(widget: Widget<*>) = true

    /**
     * Defines only the [morph] function.
     */
    companion object {

        /**
         * A convenient function for morphing from one widget. This function alters the original widget
         * instead of accepting a destination widget for the animation.
         */
        fun Widget<*>.morph(
            duration: Int = 100,
            easing: ((Double) -> Double)? = null,
            vararg updates: PropertyUpdate
        ): Animation? {
            val filteredUpdates = filter(updates.toList())

            if (findAnimation<MorphAnimation>() != null || !doesModifyState(filteredUpdates))
                return null

            return MorphAnimation(filteredUpdates, duration, easing).also { attachAnimation(it) }
        }

        /**
         * A convenient function for morphing between multiple states of a widget.
         */
        fun Widget<*>.morphBetween(
            duration: Int = 100,
            easing: ((Double) -> Double)? = null,
            first: List<PropertyUpdate>,
            second: List<PropertyUpdate>
        ) {
            if (findAnimation<MorphAnimation>() != null)
                return

            val filteredFirst = filter(first)
            val filteredSecond = filter(second)
            val destination = if (doesModifyState(filteredSecond)) filteredSecond else filteredFirst

            attachAnimation(MorphAnimation(destination, duration, easing)) {
                start()
                post { _, widget -> widget.detachAnimation<MorphAnimation>() }
            }
        }

        /**
         * Checks if the [updates] would modify the state of the [widget].
         */
        private fun Widget<*>.doesModifyState(updates: List<PropertyUpdate>) =
            updates.any { (prop, value) ->
                this::class.memberProperties.any { it.name == prop.name }
                        && prop.hasAnnotation<Interpolate>()
                        && getPropertyIn(prop, this).getter.call(this) != value
            }

        /**
         * Builds a map out of the given array of [updates] and filters out all unsuitable properties.
         */
        private fun Widget<*>.filter(updates: List<PropertyUpdate>): List<PropertyUpdate> {
            val suitable = updates
                .map { it.first }
                .filter { this::class.memberProperties.any { that -> it.name == that.name } }
                .filter { it.hasAnnotation<Interpolate>() }
                .filterIsInstance<KMutableProperty<*>>()

            if (suitable.size != updates.size) {
                LogManager.getLogger("Morph Transition").warn(
                    "${updates.size - suitable.size} propert${if (updates.size - suitable.size == 1) "y is" else "ies are"} unsuitable on ${javaClass.simpleName}!"
                )
            }

            return updates.filter { it.first in suitable }
        }

        private fun getPropertyIn(prop: KProperty<*>, obj: Any) =
            obj::class.memberProperties.first { it.name == prop.name }
    }
}