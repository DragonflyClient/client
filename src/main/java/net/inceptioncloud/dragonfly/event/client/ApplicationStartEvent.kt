package net.inceptioncloud.dragonfly.event.client

import net.inceptioncloud.dragonfly.event.Cancellable
import net.minecraft.client.main.Main

/**
 * Called when the application entry point ([Main.main]) is executed.
 *
 * @param isDeveloperMode whether the developer mode is activated via command line arguments
 */
class ApplicationStartEvent(val isDeveloperMode: Boolean) : Cancellable()