package net.inceptioncloud.dragonfly.engine.internal

import net.inceptioncloud.dragonfly.engine.GraphicsEngine
import net.inceptioncloud.dragonfly.engine.animation.Animation
import net.inceptioncloud.dragonfly.engine.animation.AttachmentBuilder
import net.inceptioncloud.dragonfly.engine.internal.annotations.*
import net.inceptioncloud.dragonfly.engine.structure.*
import net.minecraft.client.gui.Gui
import java.util.*
import kotlin.reflect.KProperty
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

/**
 * ## Widget
 *
 * A two-dimensional object is a drawable widget that has a position (x and y), a size (width and height)
 * and can receive a color. This interface provides specific methods that every 2D-object has to implement
 * in order to make drawing easier.
 *
 * Every class that implements this interface must specify its type with the type parameter `W`
 * in the interface. This allows it to return it's instance without forcing the user to use casts.
 *
 * @see IDraw
 * @param initializerBlock the block that initializes the widget and so replaces the constructor (called in
 * [WidgetIdBuilder.build])
 * @property W the type of the implementing class
 */
@Suppress("UNCHECKED_CAST", "MemberVisibilityCanBePrivate")
abstract class Widget<W : Widget<W>>(
    val initializerBlock: (W.() -> Unit)? = null
) : IDraw {

    /**
     * An object on which some operations are synchronized to provide thread-safety.
     */
    var mutex = Any()

    /**
     * Whether the widget is an internal clone. Can be used to prevent spamming the console.
     */
    var isInternalClone = false

    /**
     * Whether this widget is part of an assembled widget.
     */
    var isInAssembled = false

    /**
     * The assembled widget that this widget is part of. This value is only non-null if the widget is
     * part of an assembled widget and thus if [isInAssembled] is true.
     */
    var parentAssembled: AssembledWidget<*>? = null

    /**
     * The buffer that this widget is rendered with. This value is only non-null if the widget is directly
     * rendered by a stage and not by an assembled widget (if [isInAssembled] is false).
     */
    var parentStage: WidgetStage? = null
        set(value) {
            if (value != null) {
                handleStageAdd(value)
            }
            field = value
        }

    /**
     * Whether the widget is currently visible. If this flag is set to false, the [drawNative] method
     * won't be called by the parent [WidgetStage] that contains the widget.
     */
    var isVisible = true

    /**
     * Whether the widget is currently in a state update. This means that changes to the properties
     * will NOT result in the widget's [stateChanged] method being fired.
     */
    var isInStateUpdate = false

    /**
     * Whether the widget is currently being inspected.
     */
    var isInspected = false

    /**
     * The factor with which the widget is scaled when drawing.
     */
    @Interpolate
    var scaleFactor: Double = 1.0

    /**
     * A stacking list with all animations that are currently being applied to the widget.
     *
     * The transitions are prioritized in descending order, what means the last added animation can
     * override all animations that were applied before. To add an animation on top of the stack, use
     * [attachAnimation]. Animations in any place of the stack can be removed by calling [detachAnimation].
     */
    val animationStack: MutableList<Animation> = Collections.synchronizedList(mutableListOf<Animation>())

    /**
     * A map that contains all the names of all properties delegated by a [WidgetPropertyDelegate] and
     * their corresponding instances.
     */
    val propertyDelegates = mutableMapOf<String, WidgetPropertyDelegate<*>>()

    /**
     * A simple method that uses the widget as a receiver in order to allow changes to it during lifetime.
     *
     * This will be invoked on every tick. Before it is called, a clone will be created to which the current
     * object will then be compared. If the dynamic update changed the state of the widget, [stateChanged]
     * will be called.
     */
    private var updateDynamic: (W.() -> Unit)? = null

    /**
     * Sets the function to dynamically update the widget.
     *
     * @see updateDynamic
     */
    open fun dynamic(updateFunction: W.() -> Unit): W {
        this.updateDynamic = updateFunction
        return this as W
    }

    /**
     * The update function is called on every mod tick (regardless of the visibility state).
     *
     * It performs things like state- and dynamic updates and allows the use of animations.
     */
    open fun update() {
        if (updateDynamic != null) {
            updateDynamic?.invoke(this as W)
        }

        if (!animationStack.isNullOrEmpty()) {
            synchronized(mutex) {
                animationStack.removeAll { it.finished }
                animationStack.toTypedArray().forEach { it.tick() }
                animationStack.toTypedArray().forEach {
                    it.applyToShape(this)
                    it.companions.forEach { lambda -> lambda(this) }
                }
            }
        }
    }

    /**
     * Draws the widget or the scratchpad of the object.
     *
     * This function is a safer way to draw widget objects as it will render the [scratchpad] if one is
     * available. Without the scratchpad, animations wouldn't affect the behaviour of the widget at all!
     *
     * It suppresses deprecation-warnings at it calls the [drawNative] function that is deprecated for the
     * reason mentioned above.
     */
    @Suppress("DEPRECATION")
    fun draw() {
        GraphicsEngine.pushScale(scaleFactor)

        drawNative()

        if (isInspected && this is IPosition && (this is IDimension || this is ISize)) {
            val (width, height) = Defaults.getSizeOrDimension(this)
            Gui.drawRect(x, y, x + width, y + height, WidgetColor(0x1abc9c).apply { alphaDouble = 0.5 }.rgb)
        }

        GraphicsEngine.popScale()
    }

    /**
     * Attaches an animation on top of the animations-stack.
     *
     * This animation can override all other animations that have been added to the stack before, but will
     * be overwritten by following animations.
     */
    fun attachAnimation(animation: Animation, preferences: (AttachmentBuilder<W>.() -> Unit) = { }): W {
        val attachmentBuilder = AttachmentBuilder(animation, this)
        preferences(attachmentBuilder)
        attachmentBuilder.attach()
        return this as W
    }

    /**
     * Detaches an animation from the animations-stack.
     *
     * This method will remove the given animation from the stack, regardless of its position.
     */
    fun detachAnimation(animation: Animation): W {
        animationStack.remove(animation)
        return this as W
    }

    /**
     * Detaches all animations from the given class from the animations-stack.
     *
     * This method will remove all animations with the class from the stack, regardless of their
     * position. It is often easier than providing the animation object that should be removed.
     */
    inline fun <reified T : Animation> detachAnimation(): Boolean {
        return animationStack.removeIf { it is T }
    }

    /**
     * Tries to find an animation with the given type.
     */
    inline fun <reified T : Animation> findAnimation(): T? {
        return animationStack.firstOrNull { it is T } as? T
    }

    /**
     * Returns whether the state of the widget has been changed by a dynamic update or by an animation.
     *
     * Every widget should implement this function and adjust it to its structure, in particular the
     * properties annotated with @[Interpolate].
     *
     * @param clone the clone which the base widget should be compared to
     */
    fun isStateEqual(clone: W) = this::class.memberProperties
        .filter { it.hasAnnotation<Interpolate>() || it.hasAnnotation<State>() }
        .none { it.getter.call(this) != it.getter.call(clone) }

    /**
     * Notifies the widget that its state has been changed by a dynamic update or by an animation.
     * This is called when [isStateEqual] evaluates to false.
     */
    protected open fun stateChanged() {
        /* can be implemented by a subclass */
    }

    /**
     * Calls the [stateChanged] function while taking care of setting the [isInStateUpdate] boolean.
     */
    fun notifyStateChanged() {
        isInStateUpdate = true
        try {
            stateChanged()
        } finally {
            isInStateUpdate = false
        }
    }

    /**
     * Creates a new [WidgetPropertyDelegate] using the specified type and [initialValue]
     */
    protected fun <T> property(initialValue: T): WidgetPropertyDelegate<T> {
        val delegate = WidgetPropertyDelegate(initialValue)
        delegate.objectProperty.addListener { _, oldValue, newValue ->
            if (oldValue != newValue) {
                if (!isInStateUpdate) {
                    notifyStateChanged()
                }
            }
        }
        return delegate
    }

    /**
     * Convenient function to access the [WidgetPropertyDelegate] of a property. If the receiver
     * property isn't delegated by the a [WidgetPropertyDelegate] (its name is therefore not in
     * [propertyDelegates]) this function will return null.
     */
    fun KProperty<*>.getWidgetDelegate(): WidgetPropertyDelegate<*>? = propertyDelegates[this.name]

    /**
     * Convenient function to access the [WidgetPropertyDelegate] of a property with a specified
     * type. Will return null if the type cast fails. See [getWidgetDelegate] for more information.
     */
    fun <T> KProperty<*>.getTypedWidgetDelegate(): WidgetPropertyDelegate<T>? = propertyDelegates[this.name] as? WidgetPropertyDelegate<T>

    /**
     * Generates an info string for the widget that is used for debugging.
     */
    open fun toInfo(): List<String> = this::class.memberProperties
        .filter { it.hasAnnotation<State>() || it.hasAnnotation<Interpolate>() || it.hasAnnotation<Info>() }
        .sortedByDescending { if (it.hasAnnotation<State>()) 3 else if (it.hasAnnotation<Interpolate>()) 2 else 1 }
        .joinToString("\n") {
            it.isAccessible = true
            val name = it.name
            var value = it.getter.call(this).toString()

            if (value.length > 40) {
                value = "${value.substring(0, 40)}..."
            }

            if (it.hasAnnotation<State>())
                "--state$name = $value"
            else
                "$name = $value"
        }.split("\n")

    /**
     * Notifies the widget when the mouse is moved.
     */
    open fun handleMouseMove(data: MouseData) {
        /* can be implemented by a subclass */
    }

    /**
     * Notifies the widget when the mouse is pressed.
     */
    open fun handleMousePress(data: MouseData) {
        /* can be implemented by a subclass */
    }

    /**
     * Notifies the widget when the mouse is released.
     */
    open fun handleMouseRelease(data: MouseData) {
        /* can be implemented by a subclass */
    }

    /**
     * Notifies the widget when the mouse is dragged.
     */
    open fun handleMouseDrag(data: MouseData) {
        /* can be implemented by a subclass */
    }

    /**
     * Notifies the widget when a key on the keyboard is typed.
     */
    open fun handleKeyTyped(char: Char, keyCode: Int) {
        /* can be implemented by a subclass */
    }

    /**
     * Notifies the widget when it is added to a stage after the [initializerBlock] has been called.
     */
    open fun handleStageAdd(stage: WidgetStage) {
        /* can be implemented by a subclass */
    }

    // This function is only implemented to deprecate it in this context.
    @Deprecated(
        "This function won't render animations!",
        ReplaceWith("draw()", "net.inceptioncloud.dragonfly.engine.internal.Widget"),
        DeprecationLevel.WARNING
    )
    override fun drawNative() {
        super.drawNative()
    }
}