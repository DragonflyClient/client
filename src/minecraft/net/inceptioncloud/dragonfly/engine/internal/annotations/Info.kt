package net.inceptioncloud.dragonfly.engine.internal.annotations

import net.inceptioncloud.dragonfly.engine.internal.Widget

/**
 * Marks the annotated member as required for producing the [info text][Widget.toInfo].
 */
@Retention(AnnotationRetention.RUNTIME)
annotation class Info