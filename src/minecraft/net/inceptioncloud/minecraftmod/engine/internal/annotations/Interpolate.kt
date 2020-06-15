package net.inceptioncloud.minecraftmod.engine.internal.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks all member properties of a widget that can be interpolated.
 * <p>
 * These are required for animations like the morph transition.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Interpolate {
}
