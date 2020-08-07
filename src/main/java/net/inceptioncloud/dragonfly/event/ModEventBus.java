package net.inceptioncloud.dragonfly.event;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionHandler;
import net.inceptioncloud.dragonfly.design.DesignSubscribers;
import net.inceptioncloud.dragonfly.subscriber.DefaultSubscribers;
import net.inceptioncloud.dragonfly.versioning.updater.ApplicationStartSubscriber;
import org.apache.logging.log4j.LogManager;

/**
 * A custom {@link EventBus} for the InceptionCloud Mod Events.
 */
public class ModEventBus extends EventBus
{
    /**
     * Creates a new EventBus with the given {@link SubscriberExceptionHandler}.
     */
    public ModEventBus ()
    {
        super(new ModSubscriberExceptionHandler());

        register(new ApplicationStartSubscriber());
    }

    /**
     * Registers all subscriber methods on {@code object} to receive events.
     * Subscriber methods are selected and classified using this EventBus's
     * SubscriberFindingStrategy; the default strategy is the AnnotatedSubscriberFinder.
     *
     * @param object object whose subscriber methods should be registered.
     */
    public void register (final Object object)
    {
        super.register(object);

        LogManager.getLogger().info("Registered Event Subscriber {}", object.getClass().getName());
    }

    /**
     * Unregisters all subscriber methods on a registered {@code object}.
     *
     * @param object object whose subscriber methods should be unregistered.
     *
     * @throws IllegalArgumentException if the object was not previously registered.
     */
    @Override
    public void unregister (final Object object)
    {
        super.unregister(object);
    }

    /**
     * Posts an event to all registered subscribers.  This method will return
     * successfully after the event has been posted to all subscribers, and
     * regardless of any exceptions thrown by subscribers.
     *
     * <p>If no subscribers have been subscribed for {@code event}'s class, and
     * {@code event} is not already a {@link DeadEvent}, it will be wrapped in a
     * DeadEvent and reposted.
     *
     * @param event event to post.
     */
    @Override
    public void post (final Object event)
    {
        super.post(event);
    }
}
