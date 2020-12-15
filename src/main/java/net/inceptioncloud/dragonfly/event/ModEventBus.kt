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
        LogManager.getLogger().debug("Registered Event Subscriber {}", `object`.javaClass.name)
    }

    /**
     * Creates a new EventBus with the given [SubscriberExceptionHandler].
     */
    init {
        register(ApplicationStartSubscriber())
    }
}