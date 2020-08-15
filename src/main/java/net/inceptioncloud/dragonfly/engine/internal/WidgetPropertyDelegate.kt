package net.inceptioncloud.dragonfly.engine.internal

import javafx.beans.property.SimpleObjectProperty
import kotlin.properties.Delegates
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
     * The observable object property that holds the actual value of the delegated property and
     * that listens for changes
     */
    val objectProperty = SimpleObjectProperty<T>(initialValue)

    /**
     * 'this'-reference to the widget that uses this delegate
     */
    var thisRef: Widget<*> by Delegates.notNull()

    /**
     * Reference to the property that is delegated using this property delegate
     */
    var property: KProperty<*> by Delegates.notNull()

    /**
     * Returns the value of the observable [objectProperty]
     */
    override fun getValue(thisRef: Widget<*>, property: KProperty<*>): T {
        return objectProperty.get()
    }

    /**
     * Sets the value of the observable [objectProperty] and notifies all listeners
     */
    override fun setValue(thisRef: Widget<*>, property: KProperty<*>, value: T) {
        return objectProperty.set(value)
    }

    /**
     * Injects this delegate to the [Widget.propertyDelegates] map so it can be retrieved later and sets
     * the [thisRef] and [property] properties.
     */
    operator fun provideDelegate(thisRef: Widget<*>, prop: KProperty<*>): WidgetPropertyDelegate<T> {
        this.thisRef = thisRef
        this.property = prop

        thisRef.propertyDelegates[prop.name] = this
        return this
    }
}