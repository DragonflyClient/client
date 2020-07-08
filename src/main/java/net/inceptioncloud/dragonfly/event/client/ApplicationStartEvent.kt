package net.inceptioncloud.dragonfly.event.client

import net.inceptioncloud.dragonfly.event.Cancellable
import net.minecraft.client.main.Main

/**
 * Called when the application entry point ([Main.main]) is executed.
 */
class ApplicationStartEvent : Cancellable()