package net.inceptioncloud.dragonfly.transition.supplier;

import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

/**
 * This type of supplier transforms backward if the boolean value is true and
 * forward if the value is false.
 */
public interface BackwardForward extends IntSupplier, BooleanSupplier
{
    @Override
    default int getAsInt () {
        return getAsBoolean() ? -1 : 1;
    }
}
