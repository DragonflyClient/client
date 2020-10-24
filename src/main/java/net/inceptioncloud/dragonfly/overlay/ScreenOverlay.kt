package net.inceptioncloud.dragonfly.overlay

import com.google.common.eventbus.Subscribe
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.GraphicsEngine
import net.inceptioncloud.dragonfly.engine.GraphicsEngine.getMouseX
import net.inceptioncloud.dragonfly.engine.GraphicsEngine.getMouseY
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.animation.post
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle
import net.inceptioncloud.dragonfly.event.client.PostRenderEvent
import net.inceptioncloud.dragonfly.event.client.ResizeEvent
import net.inceptioncloud.dragonfly.event.control.KeyInputEvent
import net.inceptioncloud.dragonfly.event.control.MouseInputEvent
import net.inceptioncloud.dragonfly.mc
import net.inceptioncloud.dragonfly.overlay.toast.Toast
import net.inceptioncloud.dragonfly.ui.loader.UILoader
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Keyboard
import java.awt.Dimension
import java.lang.Double.min
import kotlin.reflect.full.companionObjectInstance

/**
 * Manages all components that are added to the screen overlay and renders them.
 */
object ScreenOverlay {

    /**
     * A widget stage that contains all components.
     */
    val stage = WidgetStage("Screen Overlay")

    /**
     * Getter for conveniently accessing the scaled dimensions of the screen.
     */
    val dimensions: Dimension
        get() {
            val scale = min(mc.displayWidth / 1920.0, mc.displayHeight / 1080.0)
            return Dimension((mc.displayWidth / scale).toInt(), (mc.displayHeight / scale).toInt())
        }

    /**
     * The action that is performed with the switch overlay.
     */
    private var overlayAction: (() -> Unit)? = null

    /**
     * Whether the client is currently switching to another gui and thus no new gui switch should
     * be started. Note that this behavior can be overwritten.
     */
    private var inSwitchProgress = false

    /**
     * Adds a component to the screen [stage].
     */
    @JvmStatic
    fun addComponent(name: String, component: Widget<*>): Widget<*> {
        stage.add(name to component)
        return component
    }

    /**
     * Displays the new [gui] screen by fading over using the switch overlay. This function respects
     * companion objects of the gui that implement the [UILoader] interface and specify loading
     * preferences.
     */
    @JvmStatic
    @JvmOverloads
    fun displayGui(gui: GuiScreen, force: Boolean = false) {
        if (inSwitchProgress && !force) return
        inSwitchProgress = true

        val prev = mc.currentScreen
        val companionObject = gui::class.companionObjectInstance

        val preload = (companionObject as? UILoader)?.shouldPreload() ?: false
        val firstDelay = if (preload) 10L else 0L
        val secondDelay = if (preload) (companionObject as UILoader).getPreloadTimeMillis() else 0L

        overlayAction = {
            GraphicsEngine.runAfter(firstDelay) {
                mc.addScheduledTask {
                    try {
                        mc.displayGuiScreen(gui)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.queue("§cError switching gui! Please try again later.", 600)
                        mc.displayGuiScreen(prev)
                    } finally {
                        GraphicsEngine.runAfter(secondDelay) {
                            finishSwitchOverlay()
                        }
                    }
                }
            }
        }

        startSwitchOverlay()
    }

    /**
     * Performs the given [overlayAction] (mostly a gui change) with a switch overlay while
     * calling it in the moment when the screen is fully covered.
     */
    private fun startSwitchOverlay() {
        val overlay = Rectangle().apply {
            stagePriority = -100
            x = 0.0f
            y = 0.0f
            width = 0.0f
            height = dimensions.height.toFloat()
            color = DragonflyPalette.accentNormal
        }

        val tail = Rectangle().apply {
            stagePriority = -101
            x = 0.0f
            y = 0.0f
            width = 0.0f
            height = dimensions.height.toFloat()
            color = DragonflyPalette.background
        }

        stage.add("switch-overlay" to overlay)
        stage.add("switch-overlay-tail" to tail)

        tail.morph(75, null, Rectangle::width to dimensions.width.toDouble())?.start()
        overlay.morph(90, null, Rectangle::width to dimensions.width.toDouble())?.start()
    }

    /**
     * Animates the switch overlay added by [startSwitchOverlay] out.
     */
    private fun finishSwitchOverlay() {
        val overlay = stage["switch-overlay"] ?: return
        val tail = stage["switch-overlay-tail"] ?: return

        overlay.detachAnimation<MorphAnimation>()
        tail.detachAnimation<MorphAnimation>()

        overlay.morph(75, null, Rectangle::x to dimensions.width.toDouble())?.start()
        tail.morph(90, null, Rectangle::x to dimensions.width.toDouble())?.post { _, _ ->
            stage.remove("switch-overlay")
            stage.remove("switch-overlay-tail")
            inSwitchProgress = false
        }?.start()
    }

    /**
     * Renders the stage on the screen.
     */
    @Subscribe
    fun onPostRender(event: PostRenderEvent) {
        stage.render()

        val overlay = stage["switch-overlay"] as? Rectangle
        if (overlay != null && overlayAction != null &&
            overlay.width == dimensions.width.toFloat() && overlay.x == 0.0f
        ) {
            stage.add("switch-overlay-full" to Rectangle().apply {
                stagePriority = -99
                x = 0.0f
                y = 0.0f
                width = dimensions.width.toFloat()
                height = dimensions.height.toFloat()
                color = DragonflyPalette.accentNormal
            })
            stage.update()
            stage.render()

            overlayAction?.invoke()
            overlayAction = null

            stage.remove("switch-overlay-full")
        }
    }

    @Subscribe
    fun onResize(event: ResizeEvent) {
        stage.remove("switch-overlay")
        stage.remove("switch-overlay-tail")
    }

    @Subscribe
    fun onKeyInput(event: KeyInputEvent) {
        if (event.press) {
            val char = Keyboard.getEventCharacter()
            stage.handleKeyTyped(char, event.key)
        }
    }

    @Subscribe
    fun onMouseInput(event: MouseInputEvent) {
        if (event.button == -1) return

        val mouseX = getMouseX().toInt()
        val mouseY = getMouseY().toInt()
        if (event.press) {
            stage.handleMousePress(MouseData(mouseX, mouseY, event.button))
        } else {
            stage.handleMouseRelease(MouseData(mouseX, mouseY, event.button))
        }
    }
}