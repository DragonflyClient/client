package net.inceptioncloud.dragonfly.event

import com.google.common.eventbus.EventBus
import net.inceptioncloud.dragonfly.versioning.updater.ApplicationStartSubscriber
import org.apache.logging.log4j.LogManager

/**
 * A custom [EventBus] for the InceptionCloud Mod Events.
 */
class ModEventBus : EventBus(ModSubscriberExceptionHandler()) {

    /**
     * Registers all subscriber methods on `object` to receive events.
     * Subscriber methods are selected and classified using this EventBus's
     * SubscriberFindingStrategy; the default strategy is the AnnotatedSubscriberFinder.
     *
     * @param object object whose subscriber methods should be registered.
     */
    override fun register(`object`: Any) {
        super.register(`object`)
        LogManager.getLogger().info("Registered Event Subscriber {}", `object`.javaClass.name)
    }

    /**
     * Unregisters all subscriber methods on a registered `object`.
     *
     * @param object object whose subscriber methods should be unregistered.
     * @throws IllegalArgumentException if the object was not previously registered.
     */
    override fun unregister(`object`: Any) {
        super.unregister(`object`)
    }

    /**
     * Posts an event to all registered subscribers.  This method will return
     * successfully after the event has been posted to all subscribers, and
     * regardless of any exceptions thrown by subscribers.
     *
     *
     * If no subscribers have been subscribed for `event`'s class, and
     * `event` is not already a [DeadEvent], it will be wrapped in a
     * DeadEvent and reposted.
     *
     * @param event event to post.
     */
    override fun post(event: Any) {
        super.post(event)
    }

    /**
     * Creates a new EventBus with the given [SubscriberExceptionHandler].
     */
    init {
        register(ApplicationStartSubscriber())
    }
}