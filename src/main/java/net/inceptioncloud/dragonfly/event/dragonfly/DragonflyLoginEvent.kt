package net.inceptioncloud.dragonfly.event.dragonfly

import net.inceptioncloud.dragonfly.account.DragonflyAccount
import net.inceptioncloud.dragonfly.event.Event

/**
 * Called when the user has logged in to a Dragonfly account.
 */
data class DragonflyLoginEvent(
    val account: DragonflyAccount
) : Event