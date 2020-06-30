package net.inceptioncloud.dragonfly.transition.supplier;

import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

/**
 * This type of supplier does nothing if the boolean value is true and
 * transforms forward if the value is false.
 */
public interface NothingForward extends IntSupplier, BooleanSupplier
{
    @Override
    default int getAsInt () {
        return getAsBoolean() ? 0 : 1;
    }
}
