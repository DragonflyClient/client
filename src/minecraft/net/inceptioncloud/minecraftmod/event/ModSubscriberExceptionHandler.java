package net.inceptioncloud.minecraftmod.event;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A custom subcriber exception handler for the {@link EventBus}.
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
        logger = Logger.getLogger("Dragonfly Event Bus");
    }

    @Override
    public void handleException (Throwable exception, SubscriberExceptionContext context)
    {
        logger.log(Level.SEVERE, "Could not dispatch event: " + context.getSubscriber() + " to " + context.getSubscriberMethod(), exception.getCause());
    }
}
