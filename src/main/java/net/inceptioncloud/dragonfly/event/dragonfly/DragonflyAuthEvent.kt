package net.inceptioncloud.dragonfly.event.dragonfly

import net.inceptioncloud.dragonfly.account.DragonflyAccount
import net.inceptioncloud.dragonfly.event.Event

/**
 * Called when a Dragonfly account has been successfully authenticated either
 * during the game start or when the user has logged in to a new one.
 */
data class DragonflyAuthEvent(
    val account: DragonflyAccount
) : Event
