package net.inceptioncloud.minecraftmod.event;

/**
 * Superclass of every event that can be cancelled when being called.
 */
public class Cancellable
{
    /**
     * Whether the event is currently cancelled.
     */
    private boolean cancelled = false;

    /**
     * @return The current cancel state of the event
     */
    public boolean isCancelled ()
    {
        return cancelled;
    }

    /**
     * Sets the cancel state of the event.
     *
     * @param cancelledIn The new state
     */
    public void setCancelled (boolean cancelledIn)
    {
        this.cancelled = cancelledIn;
    }
}
