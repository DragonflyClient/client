package net.inceptioncloud.minecraftmod.subscriber;

import net.inceptioncloud.minecraftmod.event.ModEventBus;

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
            .register(new LastServerSaveSubscriber());
    }
}
