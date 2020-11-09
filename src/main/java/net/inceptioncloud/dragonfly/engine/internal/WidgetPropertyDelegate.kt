package net.inceptioncloud.dragonfly.engine.internal

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * A widget property delegate is used to delegate a property that is created inside of a widget.
 * This property can then be observed by the widget to detect changes and re-render it to reflect
 * these changes.
 *
 * Note that this delegate can only be used inside widgets and should be created using the
 * [Widget.property] function.
 *
 * @param initialValue the initial value of the property
 */
class WidgetPropertyDelegate<T>(
    val initialValue: T
) : ReadWriteProperty<Widget<*>, T> {

    /**
     * Listeners attached to this delegate that are fired when the value changes.
     */
    private val listeners = mutableListOf<PropertyListener<T>>()

    /**
     * The current value that this delegate holds. When it changes, the [listeners] are fired.
     */
    var value: T = initialValue
        private set(value) {
            this.listeners.forEach { it.changed(field, value) }
            field = value
        }

    /**
     * Returns the [value] property as part of the delegation.
     */
    override fun getValue(thisRef: Widget<*>, property: KProperty<*>): T {
        return value
    }

    /**
     * Sets the [value] property as part of the delegation.
     */
    override fun setValue(thisRef: Widget<*>, property: KProperty<*>, value: T) {
        this.value = value
    }

    /**
     * Provides the delegate (this) and puts it in the [Widget.propertyDelegates] map.
     */
    operator fun provideDelegate(thisRef: Widget<*>, prop: KProperty<*>): WidgetPropertyDelegate<T> {
        thisRef.propertyDelegates[prop.name] = this
        return this
    }

    /**
     * Adds the [listener] to this delegate.
     */
    fun addListener(listener: PropertyListener<T>) = listeners.add(listener)

    /**
     * Removes the [listener] from this delegate.
     */
    fun destroyListener(listener: PropertyListener<T>) = listeners.remove(listener)
}