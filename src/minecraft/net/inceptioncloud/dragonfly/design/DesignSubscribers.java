package net.inceptioncloud.dragonfly.design;

import net.inceptioncloud.dragonfly.design.zoom.ZoomSubscriber;
import net.inceptioncloud.dragonfly.event.ModEventBus;

/**
 * This class registers all events for the custom client design.
 */
public class DesignSubscribers
{
    /**
     * Registers the events on the given Event Bus.
     */
    public static void register (ModEventBus modEventBus)
    {
        modEventBus.register(new ZoomSubscriber());
    }
}
