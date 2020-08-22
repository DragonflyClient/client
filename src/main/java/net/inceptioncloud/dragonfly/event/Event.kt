package net.inceptioncloud.dragonfly.event

import net.inceptioncloud.dragonfly.Dragonfly

/**
 * The event interface should be implemented by every event to provide useful extension functions.
 */
interface Event

fun Event.post(): Event {
    Dragonfly.eventBus.post(this)
    return this
}

fun Cancellable.post(): Cancellable {
    Dragonfly.eventBus.post(this)
    return this
}

fun Cancellable.checkCancellation(): Boolean {
    return isCancelled
}