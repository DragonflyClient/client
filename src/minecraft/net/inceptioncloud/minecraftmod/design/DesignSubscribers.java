package net.inceptioncloud.minecraftmod.design;

import net.inceptioncloud.minecraftmod.design.zoom.ZoomSubscriber;
import net.inceptioncloud.minecraftmod.event.ModEventBus;

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
