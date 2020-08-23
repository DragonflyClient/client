package net.inceptioncloud.dragonfly.event.client

import net.inceptioncloud.dragonfly.event.Event
import net.minecraft.util.Session

/**
 * Called when the Minecraft session changes what usually means that the Minecraft
 * account has been switched.
 */
data class SessionChangeEvent(
    val session: Session
) : Event