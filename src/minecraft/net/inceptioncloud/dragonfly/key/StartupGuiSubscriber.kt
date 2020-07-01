package net.inceptioncloud.dragonfly.key

import com.google.common.eventbus.Subscribe
import net.inceptioncloud.dragonfly.event.gui.StartupGuiEvent
import net.inceptioncloud.dragonfly.key.ui.AttachingKeyUI
import net.inceptioncloud.dragonfly.key.ui.EnterKeyUI
import net.minecraft.client.gui.GuiMainMenu
import org.apache.logging.log4j.LogManager

/**
 * Listens to the [StartupGuiEvent] and changes the target gui to the [AttachingKeyUI].
 */
object StartupGuiSubscriber {
    @Subscribe
    fun onStartupGui(event: StartupGuiEvent) {
        if (KeyStorage.isKeySaved()) {
            LogManager.getLogger().info("Validating stored key '${KeyStorage.getStoredKey()}'...")
            val result = KeyController.validateStoredKey()
            if (result.success) {
                LogManager.getLogger().info("Validation successful!")
                event.target = GuiMainMenu()
            } else {
                LogManager.getLogger().info("Validation failed: ${result.message}")
                event.target = EnterKeyUI("Error while validating stored key: " + result.message)
            }
        } else {
            LogManager.getLogger().info("Asking for Dragonfly key...")
            event.target = EnterKeyUI()
        }
    }
}