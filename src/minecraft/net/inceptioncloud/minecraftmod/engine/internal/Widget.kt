package net.inceptioncloud.minecraftmod.engine.internal

import net.inceptioncloud.minecraftmod.engine.animation.Animation
import net.inceptioncloud.minecraftmod.engine.animation.AttachmentBuilder
import net.inceptioncloud.minecraftmod.engine.internal.annotations.Interpolate
import net.inceptioncloud.minecraftmod.engine.internal.annotations.State
import net.inceptioncloud.minecraftmod.engine.structure.IDraw
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties

/**
 * ## Widget
 *
 * A two-dimensional object is a drawable widget that has a position (x and y), a size (width and height)
 * and can receive a color. This interface provides specific methods that every 2D-object has to implement
 * in order to make drawing easier.
 *
 * Every class that implements this interface must specify its type with the type parameter `Child`
 * in the interface. This allows it to return it's instance without forcing the user to use casts.
 *
 * @see IDraw
 *
 * @property Child the type of the implementing class
 */
@Suppress("UNCHECKED_CAST", "MemberVisibilityCanBePrivate")
abstract class Widget<Child : Widget<Child>> : IDraw {
    /**
     * Whether the widget is an internal clone. Can be used to prevent spamming the console.
     */
    var isInternalClone = false

    /**
     * Whether the widget is currently visible.
     *
     * If this flag is set to false, the [drawNative] method won't be called by the
     * parent [WidgetBuffer] that contains the widget.
     */
    var visible = true

    /**
     * Whether the widget is currently hovered by the mouse.
     */
    var hovered = false

    /**
     * A stacking list with all animations that are currently being applied to the widget.
     *
     * The transitions are prioritized in descending order, what means the last added animation can
     * override all animations that were applied before. To add an animation on top of the stack, use
     * [attachAnimation]. Animations in any place of the stack can be removed by calling [detachAnimation].
     */
    val animationStack = mutableListOf<Animation>()

    /**
     * The animation scratchpad of this object.
     *
     * Every widget object that contains animation has a scratchpad. The animations will be applied to
     * the scratchpad, but the base widget will still be available to support relative value updates.
     *
     * The scratchpad is created or deleted in the [update] function depending on whether the
     * [animationStack] is empty or not. When a scratchpad is available, the [draw]
     * function will draw it instead.
     */
    var scratchpad: Widget<*>? = null

    /**
     * A simple method that uses the widget as a receiver in order to allow changes to it during lifetime.
     *
     * This will be invoked on every tick. Before it is called, a clone will be created to which the current
     * object will then be compared. If the dynamic update changed the state of the widget, [stateChanged]
     * will be called.
     */
    private var updateDynamic: (Child.() -> Unit)? = null

    /**
     * Sets the function to dynamically update the widget.
     *
     * @see updateDynamic
     */
    open fun dynamic(updateFunction: Child.() -> Unit): Child {
        this.updateDynamic = updateFunction
        return this as Child
    }

    /**
     * The update function is called on every mod tick (regardless of the visibility state).
     *
     * It performs things like state- and dynamic updates and allows the use of animations.
     */
    open fun update() {
        val before = clone()

        if (updateDynamic != null) {
            updateDynamic?.invoke(this as Child)
        }

        if (!animationStack.isNullOrEmpty()) {
            scratchpad = clone().apply { isInternalClone = true }
            animationStack.removeAll { it.finished }
            animationStack.forEach { it.tick() }
            animationStack.forEach { it.applyToShape(scratchpad = scratchpad!!, base = this) }

            if (!isStateEqual(scratchpad as Child)) {
                stateChanged(scratchpad as Child)
            }
        } else scratchpad = null

        if (!isStateEqual(before)) {
            stateChanged(this)
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
        if (scratchpad != null) {
            scratchpad?.drawNative()
        } else {
            drawNative()
        }
    }

    /**
     * Attaches an animation on top of the animations-stack.
     *
     * This animation can override all other animations that have been added to the stack before, but will
     * be overwritten by following animations.
     */
    fun attachAnimation(animation: Animation, preferences: (AttachmentBuilder<Child>.() -> Unit)? = { }): Child {
        val attachmentBuilder = AttachmentBuilder(animation, this)
        preferences?.invoke(attachmentBuilder)
        attachmentBuilder.attach()
        return this as Child
    }

    /**
     * Detaches an animation from the animations-stack.
     *
     * This method will remove the given animation from the stack, regardless of its position.
     */
    fun detachAnimation(animation: Animation): Child {
        animationStack.remove(animation)
        return this as Child
    }

    /**
     * Detaches all animations from the given class from the animations-stack.
     *
     * This method will remove all animations with the class from the stack, regardless of their
     * position. It is often easier than providing the animation object that should be removed.
     */
    fun <T : Animation> detachAnimation(`class`: Class<T>): T {
        animationStack.removeIf { it.javaClass == `class` }
        return this as T
    }

    /**
     * Tries to find an animation that has the given java class.
     *
     * @param class the class of the animation
     * @return the animation or null if no one was found
     */
    fun <T : Animation> findAnimation(`class`: Class<T>): Animation? {
        return animationStack.firstOrNull { it.javaClass == `class` }
    }

    /**
     * Returns whether the state of the widget has been changed by a dynamic update or by an animation.
     *
     * Every widget should implement this function and adjust it to its structure, in particular the
     * properties annotated with @[Interpolate].
     *
     * @param clone the clone which the base widget should be compared to
     */
    fun isStateEqual(clone: Child): Boolean {
        val compare = this::class.memberProperties
            .filter { it.hasAnnotation<Interpolate>() || it.hasAnnotation<State>() }

        return compare.none {
            it.getter.call(this) != it.getter.call(clone)
        }
    }

    /**
     * Notifies the widget that its state has been changed by a dynamic update or by an animation.
     * This is called when [isStateEqual] evaluates to true.
     */
    open fun stateChanged(new: Widget<*>) {
        /* can be implemented by a subclass */
    }

    /**
     * Clones the graphics object.
     *
     * @return an identical copy of the object that the function was called on
     */
    abstract fun clone(): Child

    /**
     * Clones the graphics object and adds a padding.
     *
     * By adding a padding, the cloned object will get smaller. It stays in the center of the original
     * object, and the distance between the outline of the cloned object and the outline of the original
     * object is equal to the padding size.
     *
     * @see cloneWithMargin
     * @return a congruent copy of the object with the given padding to the original object
     */
    open fun cloneWithPadding(amount: Double): Child = Defaults.cloneWithPadding(this as Child, amount)

    /**
     * Clones the graphics object and adds a margin.
     *
     * By adding a padding, the cloned object will get greater. It stays in the center of the original
     * object, and the distance between the outline of the original object and the outline of the cloned
     * object is equal to the padding size.
     *
     * @see cloneWithPadding
     * @return a congruent copy of the object with the given margin to the original object
     */
    open fun cloneWithMargin(amount: Double): Child = Defaults.cloneWithMargin(this as Child, amount)

    /**
     * Used to create a new instance of the subclass as [Child] is the type of the subclass.
     */
    abstract fun newInstance(): Child

    /**
     * Generates an info string for the widget that is used for debugging.
     */
    abstract fun toInfo(): Array<String>

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

    // This function is only implemented to deprecate it in this context.
    @Deprecated
        (
        "This function won't render animations!",
        ReplaceWith("draw()", "net.inceptioncloud.minecraftmod.engine.internal.Widget"),
        DeprecationLevel.WARNING
    )
    override fun drawNative() {
        super.drawNative()
    }
}