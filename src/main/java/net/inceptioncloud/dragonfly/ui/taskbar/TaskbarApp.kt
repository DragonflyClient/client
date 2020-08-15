package net.inceptioncloud.dragonfly.ui.taskbar

import net.inceptioncloud.dragonfly.overlay.ScreenOverlay
import net.inceptioncloud.dragonfly.ui.taskbar.widget.TaskbarAppWidget
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ResourceLocation
import java.net.URL

/**
 * An application that can be opened via the taskbar.
 *
 * @param name the name of the application
 * @param icon the icon that represents the application
 */
abstract class TaskbarApp(val name: String, val icon: String = name.toLowerCase().replace(" ", "-")) {

    /**
     * The resource location for the [icon].
     */
    val resourceLocation = ResourceLocation("dragonflyres/icons/taskbar/apps/$icon.png")

    /**
     * Called to open the application.
     *
     * This function is invoked by the [TaskbarAppWidget] when it is clicked and should
     * somehow "open" the app. This can either be a file, an url, a gui or anything else that
     * represents an application.
     */
    abstract fun open()

    /**
     * Convenient function for opening an url.
     */
    protected fun url(url: String) {
        GuiScreen.openWebLink(URL(url).toURI())
    }

    /**
     * Convenient function for opening a gui screen with a switch overlay.
     */
    protected fun gui(gui: GuiScreen) {
        ScreenOverlay.withSwitchOverlay {
            Minecraft.getMinecraft().displayGuiScreen(gui)
        }
    }
}