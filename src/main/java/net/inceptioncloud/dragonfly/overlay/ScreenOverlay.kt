package net.inceptioncloud.dragonfly.overlay

import com.google.common.eventbus.Subscribe
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.GraphicsEngine
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.animation.post
import net.inceptioncloud.dragonfly.engine.internal.AssembledWidget
import net.inceptioncloud.dragonfly.engine.internal.WidgetStage
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle
import net.inceptioncloud.dragonfly.event.client.PostRenderEvent
import net.inceptioncloud.dragonfly.mc
import net.inceptioncloud.dragonfly.ui.loader.UILoader
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.ScaledResolution
import java.awt.Dimension
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
            val scaledResolution = ScaledResolution(Minecraft.getMinecraft())
            return Dimension(scaledResolution.scaledWidth, scaledResolution.scaledHeight)
        }

    var overlayAction: (() -> Unit)? = null

    /**
     * Adds a component to the screen [stage].
     */
    @JvmStatic
    fun addComponent(name: String, component: AssembledWidget<*>): AssembledWidget<*> {
        stage.add(name to component)
        return component
    }

    @JvmStatic
    fun displayGui(gui: GuiScreen) {
        val companionObject = gui::class.companionObjectInstance

        val preload = (companionObject as? UILoader)?.shouldPreload() ?: false
        val firstDelay = if (preload) 10L else 0L
        val secondDelay = if (preload) (companionObject as UILoader).getPreloadTimeMillis() else 0L

        overlayAction = {
            GraphicsEngine.runAfter(firstDelay) {
                mc.addScheduledTask {
                    mc.displayGuiScreen(gui)

                    GraphicsEngine.runAfter(secondDelay) {
                        finishSwitchOverlay()
                    }
                }
            }
        }

        startSwitchOverlay()
    }

    /**
     * Performs the given [action] (mostly a gui change) with a switch overlay while
     * calling it in the moment when the screen is fully covered.
     */
    private fun startSwitchOverlay() {
        val overlay = Rectangle().apply {
            stagePriority = -100
            x = 0.0
            y = 0.0
            width = 0.0
            height = dimensions.height.toDouble()
            color = DragonflyPalette.accentNormal
        }

        val tail = Rectangle().apply {
            stagePriority = -101
            x = 0.0
            y = 0.0
            width = 0.0
            height = dimensions.height.toDouble()
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
            overlay.width == dimensions.width.toDouble() && overlay.x == 0.0
        ) {

            stage.add("switch-overlay-full" to Rectangle().apply {
                stagePriority = -99
                x = 0.0
                y = 0.0
                width = dimensions.width.toDouble()
                height = dimensions.height.toDouble()
                color = DragonflyPalette.accentNormal
            })
            stage.update()
            stage.render()
            stage.render()
            stage.render()
            stage.render()
            stage.render()

            overlayAction?.invoke()
            overlayAction = null

            stage.remove("switch-overlay-full")
        }
    }
}