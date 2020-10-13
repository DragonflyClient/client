package net.inceptioncloud.dragonfly.engine.animation

import net.inceptioncloud.dragonfly.engine.internal.Widget
import org.apache.logging.log4j.LogManager

/**
 * A builder that can apply certain preferences when attaching animations
 * to a widget.
 *
 * @param Child the child type of the widget
 * @property animation the animation that will be attached
 * @property widget the widget that the animation will be attached to
 */
class AttachmentBuilder<Child : Widget<Child>>(
    private val animation: Animation,
    private val widget: Widget<Child>
) {
    /**
     * If this is `true`, the value will be automatically started when it's attached.
     */
    private var start = false

    /**
     * A convenient function to set the [start] variable to true.
     */
    fun start() {
        start = true
    }

    /**
     * Adds an action that will be executed after the animation has finished. This is done by adding
     * it to the [Animation.postActions] list.
     *
     * @param action the action that will be added, it has the animation and the widget as parameters
     */
    fun post(action: (Animation, Widget<*>) -> Unit) {
        animation.postActions.add(action)
    }

    /**
     * Sets an animation to be attached to the widget after this one has finished.
     * It is done by adding a [post] action that will attach it. You can use another [AttachmentBuilder]
     * to change the preferences of the attach.
     *
     * @param nextAnimation the animation to be attached
     * @param preferences the preferences of the attachment process
     */
    fun post(nextAnimation: Animation, preferences: (AttachmentBuilder<Child>.() -> Unit)? = null) {
        val attachBuilder = AttachmentBuilder(nextAnimation, widget)
        preferences?.invoke(attachBuilder)
        post { _, _ -> attachBuilder.attach() }
    }

    /**
     * Attaches the current [animation] to the [widget] by adding it to the [Widget.animationStack].
     * Before this is done, the [Animation.initAnimation] function will be called that returns true
     * if the animation was initialized.
     */
    fun attach() {
        if (!animation.initAnimation(widget)) {
            LogManager.getRootLogger().warn("The animation ${animation::class.simpleName} could not be attached!")
            return
        }

        if (start) {
            animation.start()
            LogManager.getLogger().debug("Started animation ${animation::class.simpleName}")
        }

        synchronized(widget) {
            widget.animationStack.add(animation)
        }
        widget.update()
    }
}