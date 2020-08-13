package net.inceptioncloud.dragonfly.overlay.hotaction

import com.google.common.eventbus.Subscribe
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.animation.companion
import net.inceptioncloud.dragonfly.engine.animation.post
import net.inceptioncloud.dragonfly.engine.internal.AssembledWidget
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseCubic
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle
import net.inceptioncloud.dragonfly.event.control.KeyDispatchEvent
import net.inceptioncloud.dragonfly.event.control.KeyInputEvent
import net.inceptioncloud.dragonfly.options.sections.OptionsSectionOverlay
import net.inceptioncloud.dragonfly.overlay.ScreenOverlay
import org.lwjgl.input.Keyboard
import java.lang.IllegalStateException
import java.util.concurrent.LinkedBlockingQueue

/**
 * Manages the appearance and interaction of hot actions.
 */
object HotAction {

    /**
     * Contains all queued hot actions
     */
    private val queue = LinkedBlockingQueue<HotActionWidget>()

    /**
     * Adds a new hot action with the specified properties to the [queue] and calls [displayNext]
     */
    fun queue(title: String, message: String, duration: Int, actions: List<Action>, allowMultipleActions: Boolean) {
        if (!OptionsSectionOverlay.enableHotActions.getKey().get())
            return

        queue.offer(HotActionWidget(
            title,
            message,
            duration,
            actions,
            allowMultipleActions
        ))
        displayNext()
    }

    /**
     * Adds the next hot action in the [queue] (if available) to the [ScreenOverlay] while providing
     * a smooth fly-in animation
     */
    fun displayNext() {
        if (ScreenOverlay.stage["hot-action"] != null || queue.isEmpty())
            return

        val next = queue.poll()

        next.runStructureUpdate()
        next.x = -next.width - 5.0
        next.runStructureUpdate()

        ScreenOverlay.addComponent("hot-action", next)
        next.morph(
            70,
            EaseCubic.IN_OUT,
            next::x to 0.0
        )?.start()
    }

    /**
     * Removes the given [hotAction] from the [ScreenOverlay] after finishing the fly-out transition
     * and calls [displayNext]
     */
    fun finish(hotAction: HotActionWidget): Unit = with(hotAction) {
        expired = true

        morph(
            70,
            EaseCubic.IN_OUT,
            ::x to -width - 5.0
        )?.post { _, _ ->
            ScreenOverlay.stage.remove("hot-action")
            displayNext()
        }?.companion { base ->
            (base as AssembledWidget).getWidget<Rectangle>("timer")?.isVisible = false
        }?.start()
    }

    /**
     * Listens to the [KeyInputEvent] to execute the actions when the dedicated trigger
     * is activated
     */
    @Subscribe
    fun onKeyType(event: KeyInputEvent) {
        if (!event.press)
            return

        val current = ScreenOverlay.stage["hot-action"] as? HotActionWidget ?: return
        val target = getTargetAction(event.key) ?: return

        current.actions.getOrNull(target - 1)?.perform?.let {
            event.isCancelled = true
            it.invoke(current)

            if (!current.allowMultipleActions) {
                finish(current)
            }
        }
    }

    /**
     * Returns the targeted action (1 - 4) depending on the selected trigger mode and key. Returns
     * null if no action was selected.
     */
    private fun getTargetAction(key: Int): Int? {
        val triggerMode = OptionsSectionOverlay.hotActionsTriggerMode.key.get()
        return if (triggerMode == 0) when (key) {
            Keyboard.KEY_F7 -> 1
            Keyboard.KEY_F8 -> 2
            Keyboard.KEY_F9 -> 3
            Keyboard.KEY_F10 -> 4
            else -> null
        } else if (triggerMode == 1 && isTriggerKeyActive()) when (key) {
            Keyboard.KEY_1 -> 1
            Keyboard.KEY_2 -> 2
            Keyboard.KEY_3 -> 3
            Keyboard.KEY_4 -> 4
            else -> null
        } else null
    }

    /**
     * Checks if the specified trigger key for the modern trigger mode is active
     */
    private fun isTriggerKeyActive(): Boolean = if (OptionsSectionOverlay.hotActionsTriggerKey.key.get() == 6) {
        (Keyboard.KEY_LCONTROL.isPressed || Keyboard.KEY_RCONTROL.isPressed) &&
                (Keyboard.KEY_LMENU.isPressed || Keyboard.KEY_RMENU.isPressed)
    } else {
        getTriggerKeyCode(OptionsSectionOverlay.hotActionsTriggerKey.key.get()).all { it.isPressed }
    }

    /**
     * Returns the key code ([Keyboard]) for the selected trigger key. Note that this function only works
     * for single-key trigger keys (not Alt Gr).
     */
    private fun getTriggerKeyCode(triggerKeyOption: Int): List<Int> = when (triggerKeyOption) {
        0 -> listOf(Keyboard.KEY_LCONTROL)
        1 -> listOf(Keyboard.KEY_RCONTROL)
        2 -> listOf(Keyboard.KEY_LMENU)
        3 -> listOf(Keyboard.KEY_RMENU)
        4 -> listOf(Keyboard.KEY_LSHIFT)
        5 -> listOf(Keyboard.KEY_RSHIFT)
        else -> throw IllegalStateException()
    }

    /**
     * Convenient function to check if a certain key is pressed via [Keyboard.isKeyDown]
     */
    private val Int.isPressed
        get() = Keyboard.isKeyDown(this)
}