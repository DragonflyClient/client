package net.inceptioncloud.dragonfly.event

import net.inceptioncloud.dragonfly.Dragonfly

/**
 * The event interface should be implemented by every event to provide useful extension functions.
 */
interface Event

/**
 * Posts the event to the [Dragonfly.eventBus] returning the event itself.
 */
fun <T : Event> T.post(): T {
    Dragonfly.eventBus.post(this)
    return this
}