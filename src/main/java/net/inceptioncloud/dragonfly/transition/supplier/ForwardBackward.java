package net.inceptioncloud.dragonfly.transition.supplier;

import java.util.function.*;

/**
 * This type of supplier transforms forward if the boolean value is true and
 * backward if the value is false.
 */
public interface ForwardBackward extends IntSupplier, BooleanSupplier {
    @Override
    default int getAsInt() {
        return getAsBoolean() ? 1 : -1;
    }
}
