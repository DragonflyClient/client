package net.inceptioncloud.minecraftmod.engine.internal.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks all member properties of a widget represent the state of the widget.
 * <p>
 * They are used to compare the state of two widgets while member properties without
 * this annotation are seen as utility and skipped during the process.
 * <p>
 * This annotation is only required for properties that are not annotated with {@link Interpolate}.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface State {
}
