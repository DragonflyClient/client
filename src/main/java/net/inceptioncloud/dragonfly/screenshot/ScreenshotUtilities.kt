package net.inceptioncloud.dragonfly.screenshot

import kotlinx.coroutines.*
import net.inceptioncloud.dragonfly.options.sections.OptionsSectionClient
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiNewChat
import net.minecraft.event.ClickEvent
import net.minecraft.util.ChatComponentText
import net.minecraft.util.ChatStyle
import java.awt.Desktop
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import javax.imageio.ImageIO

/**
 * Provides some utility functions to the screenshot system.
 */
object ScreenshotUtilities {

    /**
     * Whether the utilities are enabled by the Dragonfly configuration.
     */
    private val isUtilitiesEnabled: Boolean
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
                    .appendSibling(callbackMessage("§8[§eSave§8]") { savePermanently(image, file) })
                    .appendText(" ")
                    .appendSibling(callbackMessage("§8[§9Open§8]") { open(image, file) })
                    .appendText(" ")
                    .appendSibling(callbackMessage("§8[§aCopy§8]") { copy(image) })
                    .appendText(" ")
                    .appendSibling(callbackMessage("§8[§cUpload§8]") { upload(image) })
            )
        }

        return true
    }

    /**
     * Uploads the given [image] to Imgur via their API.
     */
    private fun upload(image: BufferedImage) {
        GlobalScope.launch(Dispatchers.IO) {
            val chatGUI = Minecraft.getMinecraft().ingameGUI.chatGUI
            val bos = ByteArrayOutputStream().also { ImageIO.write(image, "png", it) }
            val bytes = bos.toByteArray()
            val result = khttp.post(
                url = "https://api.imgur.com/3/image",
                headers = mapOf(
                    "Authorization" to "Client-ID c78854cfb8f4e5d"
                ),
                data = mapOf(
                    "image" to Base64.getEncoder().encodeToString(bytes).also { println("==> $it") }
                )
            )

            if (result.statusCode == 200) {
                val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard
                clipboard.setContents(
                    StringSelection(result.jsonObject.getJSONObject("data").getString("link")),
                    null
                )
                chatGUI.printChatMessage("§8·•● §6Dragonfly §8» §7Your screenshot has been successfully " +
                        "uploaded and the link has been copied to your clipboard.")
            } else {
                chatGUI.printChatMessage("§8·•● §6Dragonfly §8» §7Failed to upload screenshot to Imgur§8: " +
                        "§cHTTP status code ${result.statusCode}")
            }
        }
    }

    /**
     * Copies the [image] to the clipboard.
     */
    private fun copy(image: BufferedImage) {
        GlobalScope.launch(Dispatchers.IO) {
            val chatGUI = Minecraft.getMinecraft().ingameGUI.chatGUI
            val trans = TransferableImage(image)
            val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard
            clipboard.setContents(trans, null)
            chatGUI.printChatMessage("§8·•● §6Dragonfly §8» §7Your screenshot has been copied to your clipboard.")
        }
    }

    /**
     * Opens the screenshot in the associated program after writing the [image] to a
     * temporary file in the Minecraft home directory.
     */
    private fun open(image: BufferedImage, file: File) {
        GlobalScope.launch(Dispatchers.IO) {
            val screenshotFile = file.takeIf { it.exists() } ?: saveTemporary(image, file)
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(screenshotFile)
            }
        }
    }

    /**
     * Writes the given [image] to the [file] and so storing it permanently in the desired
     * screenshots folder.
     */
    @Suppress("BlockingMethodInNonBlockingContext")
    private fun savePermanently(image: BufferedImage, file: File) {
        GlobalScope.launch (Dispatchers.IO) {
            val chatGUI = Minecraft.getMinecraft().ingameGUI.chatGUI
            ImageIO.write(image, "png", file)
            chatGUI.printChatMessage("§8·•● §6Dragonfly §8» §7Your screenshot has been saved to your device.")
        }
    }

    /**
     * Writes the given [image] to a temporary file with the name of the [file] to store it
     * temporary. Returns the created temporary file.
     */
    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun saveTemporary(image: BufferedImage, file: File): File {
        return withContext(Dispatchers.IO) {
            val directory = File(Minecraft.getMinecraft().mcDataDir, "temp")
            directory.mkdir()

            val temp = File(directory, file.name)
            ImageIO.write(image, "png", temp)

            temp
        }
    }

    /**
     * A convenient function for creating a chat component using a click event action of
     * [ClickEvent.Action.RUN_CALLBACK].
     */
    private fun callbackMessage(message: String, callback: () -> Unit) =
        ChatComponentText(message).setChatStyle(ChatStyle().setChatClickEvent(ClickEvent(callback)))

    /**
     * Convenient function for printing a [message] to the chat.
     */
    private fun GuiNewChat.printChatMessage(message: String) = printChatMessage(ChatComponentText(message))
}