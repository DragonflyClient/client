package net.inceptioncloud.dragonfly.transition.supplier;

import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

/**
 * This type of supplier transforms backward if the boolean value is true and
 * does nothing if the value is false.
 */
public interface BackwardNothing extends IntSupplier, BooleanSupplier
{
    @Override
    default int getAsInt () {
        return getAsBoolean() ? -1 : 0;
    }
}
