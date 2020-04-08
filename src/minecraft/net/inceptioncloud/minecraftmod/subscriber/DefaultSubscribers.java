package net.inceptioncloud.minecraftmod.subscriber;

import net.inceptioncloud.minecraftmod.event.ModEventBus;
import net.inceptioncloud.minecraftmod.tracking.transitions.FileSaveSubscriber;
import net.inceptioncloud.minecraftmod.tracking.transitions.TickSubscriber;

/**
 * Registers the Default Event Subscribers.
 */
public class DefaultSubscribers
{
    /**
     * Performs the registration.
     */
    public static void register (ModEventBus modEventBus)
    {
        modEventBus
            .registerAnd(new AuthenticationSubscriber())
            .registerAnd(new FileSaveSubscriber())
            .registerAnd(new TickSubscriber())
            .register(new LastServerSaveSubscriber());
    }
}
