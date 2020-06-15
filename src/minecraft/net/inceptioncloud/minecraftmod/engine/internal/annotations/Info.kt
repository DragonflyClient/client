package net.inceptioncloud.minecraftmod.engine.internal.annotations

import net.inceptioncloud.minecraftmod.engine.internal.Widget

/**
 * Marks the annotated member as required for producing the [info text][Widget.toInfo].
 */
@Retention(AnnotationRetention.RUNTIME)
annotation class Info