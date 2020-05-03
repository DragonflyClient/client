package net.inceptioncloud.minecraftmod.engine.internal;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * An annotation to support dynamic state changes to widgets.
 * <p>
 * Every member property in a {@link Widget} object that is annotated with this class
 * can be changed by a dynamic update or an animation update.
 */
@Retention (RetentionPolicy.RUNTIME)
public @interface Dynamic
{
}
