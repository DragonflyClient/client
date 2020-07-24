package net.inceptioncloud.dragonfly.event;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A custom subscriber exception handler for the {@link EventBus}.
 */
public class ModSubscriberExceptionHandler implements SubscriberExceptionHandler
{
    /**
     * Logger for event dispatch failures.  Named by the fully-qualified name of
     * this class, followed by the identifier provided at construction.
     */
    private final Logger logger;

    /**
     * Custom Constructor
     */
    public ModSubscriberExceptionHandler ()
    {
        logger = LogManager.getLogger("Event Dispatcher");
    }

    @Override
    public void handleException (Throwable exception, SubscriberExceptionContext context)
    {
        logger.error("Could not dispatch event: " + context.getSubscriber() + " to " + context.getSubscriberMethod(), exception.getCause());
    }
}
