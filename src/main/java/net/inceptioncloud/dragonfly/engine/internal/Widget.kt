package net.inceptioncloud.dragonfly.engine.internal

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import net.inceptioncloud.dragonfly.engine.GraphicsEngine
import net.inceptioncloud.dragonfly.engine.animation.Animation
import net.inceptioncloud.dragonfly.engine.animation.AttachmentBuilder
import net.inceptioncloud.dragonfly.engine.structure.*
import net.inceptioncloud.dragonfly.engine.tooltip.Tooltip
import net.inceptioncloud.dragonfly.mc
import net.inceptioncloud.dragonfly.overlay.modal.Modal
import net.inceptioncloud.dragonfly.utils.Keep
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import java.util.*
import kotlin.reflect.KProperty

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
     * Whether the widget is an internal clone. Can be used to prevent spamming the console.
     */
    var isInternalClone = false

    /**
     * Whether this widget is part of an assembled widget.
     */
    var isInAssembled = false

    /**
     * Whether the widget is part of a modal widget. This property is overwritten by
     * [AssembledWidget] to also change the value of it's children.
     */
    open var isModal = false

    /**
     * Whether this widget ignores the [FocusHandler] of the current screen.
     */
    open var overrideFocusHandler = false

    /**
     * The assembled widget that this widget is part of. This value is only non-null if the widget is
     * part of an assembled widget and thus if [isInAssembled] is true.
     */
    var parentAssembled: AssembledWidget<*>? = null
        set(value) {
            if (value != null) {
                handleAssembledAdd(value)
            }
            field = value
        }

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
     * The id that has been assigned to this widget when it was added to the [parentStage] or to its
     * [parentAssembled] widget.
     */
    var widgetId: String? = null

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
     * The priority of the widget on the stage. Defines the order in which the widgets are drawn
     * and in which events are passed to them. Ascending order.
     */
    var stagePriority: Int = 0

    /**
     * The factor with which the widget is scaled when drawing.
     */
    var scaleFactor: Float = 1.0F

    /**
     * Whether the widget is currently hovered.
     */
    var isHovered: Boolean = false

    /**
     * The default action that is executed when the widget is clicked.
     */
    var clickAction: () -> Unit = {}

    /**
     * The default action that is executed when the widget is hovered.
     */
    var hoverAction: (Boolean) -> Unit = {}

    /**
     * An optional tooltip that is shown when the widget is hovered to provide additional information
     * about its behavior to the user.
     */
    var tooltip: Tooltip? = null
        set(value) {
            if (field != null) {
                parentStage?.remove("$value::tooltip")
                parentAssembled?.structure?.remove("$value::tooltip")
            }
            field = value
            if (value != null) {
                value.host = this
                value.prepare()
            }
        }

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
            synchronized(animationStack) {
                animationStack.removeAll { it.finished }
                animationStack.toTypedArray().forEach {
                    it.applyToWidget(this)
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
        GlStateManager.pushMatrix()
        GlStateManager.scale(scaleFactor)

        if (this is IPosition && mc.currentScreen != null) {
            GlStateManager.translate(x * (1 / scaleFactor) - x, y * (1 / scaleFactor) - y, 0.0F)
        }

        drawNative()

        if (isInspected && this is IPosition && (this is IDimension || this is ISize)) {
            val (width, height) = Defaults.getSizeOrDimension(this)
            Gui.drawRect(x, y, x + width, y + height, WidgetColor(0x1abc9c).apply { alphaFloat = 0.5F }.rgb)
        }

        GlStateManager.popMatrix()

        val focusHandler = mc.currentScreen?.takeIf { parentStage == it.stage }?.focusHandler
        val mouseX = GraphicsEngine.getMouseX()
        val mouseY = GraphicsEngine.getMouseY()
        val data = MouseData(mouseX.toInt(), mouseY.toInt())

        if (canUpdateHoverState() && !(Modal.isModalPresent() && !isModal)
            && (focusHandler?.captureMouseFocus(data) != true || overrideFocusHandler)
            && this is IPosition && (this is IDimension || this is ISize)
        ) {
            val (width, height) = Defaults.getSizeOrDimension(this)

            if (mouseX in x..x + width && mouseY in y..y + height) {
                if (isHovered)
                    return

                isHovered = true
                handleHoverStateUpdate()
            } else {
                if (!isHovered)
                    return

                isHovered = false
                handleHoverStateUpdate()
            }
        }
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
     * Notifies the widget that its state has been changed by a dynamic update or by an animation.
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
            tooltip?.prepare()
        } finally {
            isInStateUpdate = false
        }
    }

    /**
     * Creates a new [WidgetPropertyDelegate] using the specified type and [initialValue]
     */
    protected fun <T> property(initialValue: T): WidgetPropertyDelegate<T> {
        val delegate = WidgetPropertyDelegate(initialValue)
        delegate.objectProperty.addListener(WidgetListener(this))
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
        if (isHovered) {
            clickAction()
        }
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
        tooltip?.let { stage.add("$widgetId::tooltip" to it.widget) }
    }

    /**
     * Notifies the widget when it is added to an assembled widget.
     */
    open fun handleAssembledAdd(parent: AssembledWidget<*>) {
        tooltip?.let { parent.structure.put("$widgetId::tooltip", it.widget) }
    }

    /**
     * Called when the hover state of a widget changes.
     */
    open fun handleHoverStateUpdate() {
        hoverAction(isHovered)
        tooltip?.animateTooltip(isHovered)
    }

    /**
     * Called when the widget searches for changes in the hover state. When this evaluates to
     * true, the hover state can be changed, otherwise changes are ignored.
     */
    open fun canUpdateHoverState(): Boolean {
        /* can be implemented by a subclass */
        return true
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

@Keep
private class WidgetListener<T>(val widget: Widget<*>) : ChangeListener<T> {
    override fun changed(observable: ObservableValue<out T>?, oldValue: T, newValue: T) {
        if (oldValue != newValue) {
            if (!widget.isInStateUpdate) {
                widget.notifyStateChanged()
            }
        }
    }
}