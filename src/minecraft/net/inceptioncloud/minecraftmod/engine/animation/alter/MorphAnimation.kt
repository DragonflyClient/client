package net.inceptioncloud.minecraftmod.engine.animation.alter

import net.inceptioncloud.minecraftmod.engine.animation.Animation
import net.inceptioncloud.minecraftmod.engine.internal.Dynamic
import net.inceptioncloud.minecraftmod.engine.internal.Widget
import net.inceptioncloud.minecraftmod.engine.sequence.Sequence
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.hasAnnotation

class MorphAnimation(val destination: Widget<*>, val duration: Int = 100, val easing: ((Double) -> Double)? = null) :
    Animation() {

    private fun KClass<*>.getPropertyByName(name: String): KProperty<*> =
        declaredMemberProperties.first { it.name == name }

    private val propertySequences: MutableMap<KMutableProperty<*>, Sequence<*>> = mutableMapOf()

    override fun initAnimation(parent: Widget<*>): Boolean {
        var endHookApplied = false

        return if (super.initAnimation(parent)) {

            parent::class.declaredMemberProperties
                .filter { it.hasAnnotation<Dynamic>() && it is KMutableProperty<*> }
                .forEach {
                    val initialValue = it.getter.call(parent)
                    val destinationValue = destination::class.getPropertyByName(it.name).getter.call(destination)
                    var sequence =
                        Sequence.generateSequence(initialValue, destinationValue, duration).withEasing(easing)

                    if (!endHookApplied) {
                        sequence = sequence.withEndHook { finish() }
                        endHookApplied = true
                    }

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
    }

    override fun tick() {
        if (!running)
            return

        propertySequences.values.forEach { it.next() }
    }

    override fun isApplicable(widget: Widget<*>) = widget::class == destination::class
}