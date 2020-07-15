package net.inceptioncloud.dragonfly.screenshot

import net.inceptioncloud.dragonfly.options.sections.OptionsSectionClient
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiNewChat
import net.minecraft.client.gui.GuiScreen
import net.minecraft.event.ClickEvent
import net.minecraft.util.ChatComponentText
import net.minecraft.util.ChatStyle
import java.awt.Desktop
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.image.BufferedImage
import java.io.File

/**
 * Provides some utility functions to the screenshot system.
 */
object ScreenshotUtilities {

    /**
     * Whether the utilities are enabled by the Dragonfly configuration.
     */
    val isUtilitiesEnabled: Boolean
        get() = OptionsSectionClient.screenshotUtilities.getKey().get()

    /**
     * Called after a screenshot has been created. If this function returns true, the screenshot
     * will not be shown in the chat with the default message.
     */
    @JvmStatic
    fun screenshotTaken(image: BufferedImage, file: File): Boolean {
        if (!isUtilitiesEnabled)
            return false

        val chatGUI = Minecraft.getMinecraft().ingameGUI.chatGUI
        with(chatGUI) {
            printChatMessage("§8·•● §6Dragonfly §8» §7Your screenshot has been saved§8!")
            printChatMessage(
                ChatComponentText("§8·•● §6Dragonfly §8» ")
                    .appendSibling(CallbackComponent("§8[§9Open§8]") {
                        if (Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().open(file)
                        }
                    }).appendText(" ")
                    .appendSibling(CallbackComponent("§8[§eCopy§8]") {
                        val trans = TransferableImage(image)
                        val c: Clipboard = Toolkit.getDefaultToolkit().systemClipboard
                        c.setContents(trans, null)
                    }).appendText(" ")
            )
        }

        return true
    }

    private fun CallbackComponent(message: String, callback: () -> Unit) =
        ChatComponentText(message).setChatStyle(ChatStyle().setChatClickEvent(ClickEvent(callback)))

    fun GuiNewChat.printChatMessage(message: String) = printChatMessage(ChatComponentText(message))
}