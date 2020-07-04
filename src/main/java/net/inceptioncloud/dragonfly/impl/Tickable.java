package net.inceptioncloud.dragonfly.impl;

/**
 * The tickable interface provides a method that is called on every mod tick.
 */
public interface Tickable
{
    /**
     * Handle the mod tick.
     */
    void modTick ();
}
