package net.inceptioncloud.dragonfly.screenshot

import kotlinx.coroutines.*
import net.inceptioncloud.dragonfly.options.sections.OptionsSectionClient
import net.inceptioncloud.dragonfly.overlay.hotaction.Action
import net.inceptioncloud.dragonfly.overlay.hotaction.HotAction
import net.inceptioncloud.dragonfly.overlay.toast.Toast
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

        HotAction.queue(
            title = "Screenshot Utilities",
            message = "A screenshot has been created. Do you wish to take further actions?",
            duration = 1500,
            actions = listOf(
                Action("Save") { savePermanently(image, file) },
                Action("Open") { open(image, file) },
                Action("Copy") { copy(image) },
                Action("Upload") { upload(image) }
            ),
            allowMultipleActions = true
        )

        return true
    }

    /**
     * Uploads the given [image] to Imgur via their API.
     */
    private fun upload(image: BufferedImage) {
        Toast.queue("Uploading screenshot...", 400)
        GlobalScope.launch(Dispatchers.IO) {
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
                Toast.queue("Upload successful! Link copied to clipboard.", 1000)
            } else {
                Toast.queue("Upload failed! HTTP status code: ${result.statusCode}", 1000)
            }
        }
    }

    /**
     * Copies the [image] to the clipboard.
     */
    private fun copy(image: BufferedImage) {
        GlobalScope.launch(Dispatchers.IO) {
            val trans = TransferableImage(image)
            val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard
            clipboard.setContents(trans, null)
            Toast.queue("Screenshot copied to clipboard", 1000)
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
        GlobalScope.launch(Dispatchers.IO) {
            ImageIO.write(image, "png", file)
            Toast.queue("Screenshot saved to device", 1000)
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
}