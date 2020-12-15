package net.inceptioncloud.dragonfly.event;

import com.google.common.eventbus.*;
import org.apache.logging.log4j.*;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * A custom subscriber exception handler for the {@link EventBus}.
 */
public class ModSubscriberExceptionHandler implements SubscriberExceptionHandler {
    /**
     * Logger for event dispatch failures.  Named by the fully-qualified name of
     * this class, followed by the identifier provided at construction.
     */
    private final Logger logger;

    /**
     * Custom Constructor
     */
    public ModSubscriberExceptionHandler() {
        logger = LogManager.getLogger("Event Dispatcher");
    }

    @Override
    public void handleException(Throwable exception, SubscriberExceptionContext context) {
        final Object event = context.getEvent();
        final Method method = context.getSubscriberMethod();
        logger.error("Could not dispatch event " + event.getClass().getSimpleName()
                + " to " + method.getDeclaringClass().getName() + "." + method.getName() + "()");
        logger.error(exception);
        exception.printStackTrace();
    }
}
